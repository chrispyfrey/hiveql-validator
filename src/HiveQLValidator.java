/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.lang.Math;

import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class HiveQLValidator {
    private static String removeComments(String query) {
        return query.replaceAll("--.*", "").trim();
    }

    private static String removeUseStatements(String query) {
        return query.replaceAll("\\buse\\b(.|\n)*?;", "").trim();
    }

    private static String removeSetStatements(String query) {
        return query.replaceAll("\\bset\\b(.|\\n)*?;", "").trim();
    }

    private static String removeAddJarStatements(String query) {
        return query.replaceAll("add jar(.|\\n)*?;", "").trim();
    }

    private static String removeTempFunctions(String query) {
        return query.replaceAll("create temporary function(.|\\n)*?;", "").trim();
    }

    private static String removeVariables(String query) {
        return query.replaceAll("\\$\\{.{1,10}:.{1,30}\\}\\.{0,1}", "").trim();
    }

    private static String[] extractQueries(String query) {
        query = query.toLowerCase();
        query = removeComments(query); // Comments should be removed first to strip unexpected keywords
        query = removeUseStatements(query);
        query = removeSetStatements(query);
        query = removeAddJarStatements(query);
        query = removeTempFunctions(query);
        query = removeVariables(query);
        return query.split(";");
    }

    private static String getHql(String filePath) {
        byte[] fileBytes = null;

        try {
            fileBytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("\n[ERROR]: Could not read .hql file. Please check the passed filepath.");
            e.printStackTrace();
            System.exit(1);
        }

        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].length() < 5 || !args[0].substring(args[0].length()-4, args[0].length()).equals(".hql")) {
            System.out.println("\n[ERROR]: An .hql filepath must be passed to this program as the first argument.");
            System.exit(1);
        }

        String[] splitFp = args[0].split("/");
        String hqlFileName = splitFp[splitFp.length-1];
        System.out.println(String.format("\n[INFO]: Opening %s for syntax validation.", hqlFileName));

        String queryString = getHql(args[0]);
        String[] queryArray = extractQueries(queryString);

        ParseDriver parseDriver = new ParseDriver(); // This is what raises the SLF4J warning
        System.out.println("");

        String querySample = "";
        int queryNum = 1;

        try {
            for (String query : queryArray) {
                querySample = query.substring(0, Math.min(query.length(), 75)).replaceAll("\\s{1,}", " ").trim();
                System.out.println(String.format("[INFO]: Validating query #%d...\n[INFO]: %s...", queryNum, querySample));
                parseDriver.parse(query);
                System.out.println(String.format("[INFO]: Query #%d passed syntax validation.\n", queryNum, querySample));
                ++queryNum;
            }
        } catch (ParseException e) {
            System.out.println(String.format("\n[ERROR]: Error validating query #%d...\n[ERROR]: %s...", queryNum, querySample));
            System.out.println(String.format("[ERROR]: %s", e));
            //e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println(String.format("[INFO]: All queries in %s have passed HiveQL syntax validations", hqlFileName));
    }
}
