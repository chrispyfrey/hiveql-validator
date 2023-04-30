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
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;
import java.lang.Math;

import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class HiveQLValidator {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].length() < 5 || !args[0].substring(args[0].length()-4, args[0].length()).equals(".hql")) {
            System.out.println("[ERROR]: An .hql filepath must be passed to this program as the first argument.");
            System.exit(1);
        }

        byte[] fileBytes = null;
        String[] splitFp = args[0].split("/");
        String hqlFileName = splitFp[splitFp.length-1];
        System.out.println(String.format("\n[INFO]: Opening %s for syntax validation.\n", hqlFileName));

        try {
            fileBytes = Files.readAllBytes(Paths.get(args[0]));
        } catch (IOException e) {
            System.out.println("[ERROR]: Could not read .hql file. Please check the passed filepath.");
            e.printStackTrace();
            System.exit(1);
        }

        String queryString = new String(fileBytes, StandardCharsets.UTF_8);
        String[] queryArray = queryString.split(";");
        Pattern pattern = Pattern.compile("\\bset\\b");
        String tmp = null;

        for (int i = 0; i < queryArray.length; ++i) {
            tmp = queryArray[i].toLowerCase();
            tmp = tmp.replaceAll("--.+(\\n|$)", "").trim();

            if (pattern.matcher(tmp).find() || tmp.contains("add jar") || tmp.contains("temporary function") || tmp.equals("")) {
                queryArray[i] = null;
            }
            else {
                queryArray[i] = queryArray[i].replaceAll("--.+(\\n|$)", "");
                queryArray[i] = queryArray[i].replaceAll("\\$\\{.{1,10}:.{1,30}\\}\\.{0,1}", "").trim();

                if (queryArray[i].equals(""))
                    queryArray[i] = null;
            }
        }

        ParseDriver parseDriver = new ParseDriver(); // This is what raises the SLF4J warning
        System.out.println("");

        String querySample = "";
        int queryNum = 1;

        try {
            for (String query : queryArray) {
                if (query != null) {
                    querySample = query.substring(0, Math.min(query.length(), 75)).replaceAll("\\s{1,}", " ").trim();
                    System.out.println(String.format("[INFO]: Validating query #%d...\n[INFO]: %s...", queryNum, querySample));
                    parseDriver.parse(query);
                    System.out.println(String.format("[INFO]: Query #%d passed syntax validation.\n", queryNum, querySample));
                    ++queryNum;
                }
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
