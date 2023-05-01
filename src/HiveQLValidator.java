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
    private ParseDriver parseDriver;
    private String hiveQL;
    private String hqlFileName;

    private String commentPattern = "--.*";
    private String usePattern = "\\buse\\b(.|\n)*?;";
    private String setPattern = "\\bset\\b(.|\\n)*?;";
    private String addJarPattern = "add jar(.|\\n)*?;";
    private String tempFuncPattern = "create temporary function(.|\\n)*?;";
    private String dbNamePattern = "\\$\\{.*?:.*?}.*?\\.";
    private String variablePattern = "\\$\\{.*?:.*?\\}";

    public HiveQLValidator(String filePath) {
        byte[] fileBytes = null;
        String[] splitFp = filePath.split("/");
        this.hqlFileName = splitFp[splitFp.length-1];
        System.out.println(String.format("\n[INFO]: Opening %s for syntax validation.", this.hqlFileName));

        try {
            fileBytes = Files.readAllBytes(Paths.get(filePath));
        }
        catch (IOException e) {
            System.out.println("\n[ERROR]: Could not read .hql file. Please check the passed filepath.");
            e.printStackTrace();
            System.exit(1);
        }

        this.hiveQL = new String(fileBytes, StandardCharsets.UTF_8);
        this.parseDriver = new ParseDriver();
    }

    private String removeComments(String query) {
        return query.replaceAll(this.commentPattern, "").trim();
    }

    private String removeUseStatements(String query) {
        return query.replaceAll(this.usePattern, "").trim();
    }

    private String removeSetStatements(String query) {
        return query.replaceAll(this.setPattern, "").trim();
    }

    private String removeAddJarStatements(String query) {
        return query.replaceAll(this.addJarPattern, "").trim();
    }

    private String removeTempFunctions(String query) {
        return query.replaceAll(this.tempFuncPattern, "").trim();
    }

    private String removeDbNames(String query) {
        return query.replaceAll(this.dbNamePattern, "").trim();
    }

    private String removeVariables(String query) {
        return query.replaceAll(this.variablePattern, "placeholder").trim();
    }

    private String[] extractQueries(String hqlString) {
        hqlString = hqlString.toLowerCase();
        hqlString = this.removeComments(hqlString);
        hqlString = this.removeUseStatements(hqlString);
        hqlString = this.removeSetStatements(hqlString);
        hqlString = this.removeAddJarStatements(hqlString);
        hqlString = this.removeTempFunctions(hqlString);
        hqlString = this.removeDbNames(hqlString);
        hqlString = this.removeVariables(hqlString);
        return hqlString.split(";");
    }

    public void validateHQL() {
        String[] queryArray = this.extractQueries(this.hiveQL);
        String querySample = "";
        int queryNum = 1;

        try {
            for (String query : queryArray) {
                if (!query.trim().equals("")) {
                    querySample = query.substring(0, Math.min(query.length(), 75)).replaceAll("\\s{2,}", " ").trim();
                    System.out.println(String.format("\n[INFO]: Validating query #%d...\n[INFO]: %s...", queryNum, querySample));
                    this.parseDriver.parse(query);
                    System.out.println(String.format("[INFO]: Query #%d passed syntax validation.", queryNum, querySample));
                }
                else {
                    System.out.println("[INFO]: Query #%d is empty.\n");
                }
                ++queryNum;
            }
        }
        catch (ParseException e) {
            System.out.println(String.format("\n[ERROR]: Error validating query #%d...\n[ERROR]: %s...", queryNum, querySample));
            System.out.println(String.format("[ERROR]: %s", e));
            System.exit(1);
        }
        
        System.out.println(String.format("[INFO]: All queries in %s have passed HiveQL syntax validations", this.hqlFileName));
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0].length() < 5 || !args[0].substring(args[0].length()-4, args[0].length()).equals(".hql")) {
            System.out.println("\n[ERROR]: An .hql filepath must be passed to this program as the first argument.");
            System.exit(1);
        }

        HiveQLValidator hiveQlValidator = new HiveQLValidator(args[0]);
        hiveQlValidator.validateHQL();
    }
}
