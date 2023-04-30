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

import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class HiveQLValidator {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].length() < 5 || !args[0].substring(args[0].length()-4, args[0].length()).equals(".hql")) {
            System.out.println("Error: An .hql filepath must be passed to this program as the first argument.");
            System.exit(1);
        }

        byte[] fileBytes = null;

        try {
            fileBytes = Files.readAllBytes(Paths.get(args[0]));
        } catch (IOException e) {
            System.out.println("Error: Could not read .hql file. Please check the passed filepath.");
            e.printStackTrace();
            System.exit(1);
        }

        String queryString = new String(fileBytes, StandardCharsets.UTF_8);
        String[] queryArray = queryString.split(";");
        String tmp = null;

        for (int i = 0; i < queryArray.length; ++i) {
            tmp = queryArray[i].toLowerCase();
            
            if ((tmp.contains("set") && tmp.contains("=")) || (tmp.contains("add jar") && tmp.contains(".jar"))) {
                queryArray[i] = null;
            } 
            else {
                queryArray[i] = tmp.replaceAll("\\$\\{.+:.+\\}\\.?", "");
            }
        }

        ParseDriver parseDriver = new ParseDriver();

        try {
            for (String query : queryArray) {
                if (query != null) {
                    parseDriver.parse(query);
                }
            }
        } catch (ParseException e) {
            System.out.println("Error: Could not parse query.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("No issues detected with query.");
    }
}
