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
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.lang.Math;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class HiveQLValidator {
    private ParseDriver parseDriver;
    private ArrayList<String> hiveQLList;
    private ArrayList<String> hqlFileNameList;
    private String filePath;
    private boolean isDirectory;

    private String commentPattern = "--.*";
    private String usePattern = "\\buse\\b(.|\n)*?;";
    private String setPattern = "\\bset\\b(.|\\n)*?;";
    private String addJarPattern = "add jar(.|\\n)*?;";
    private String tempFuncPattern = "create temporary function(.|\\n)*?;";
    private String dbNamePattern = "\\$\\{.*?:.*?}.*?\\.";
    private String variablePattern = "\\$\\{.*?:.*?\\}";

    public HiveQLValidator(String filePath, boolean isDir) {
        this.parseDriver = new ParseDriver();
        this.hiveQLList = new ArrayList<>();
        this.hqlFileNameList = new ArrayList<>();
        this.filePath = filePath;
        this.isDirectory = isDir;

        ArrayList<byte[]> fileBytesList = new ArrayList<>();
        String[] splitFp;

        try {
            if (!isDir) {
                fileBytesList.add(Files.readAllBytes(Paths.get(filePath)));
                splitFp = filePath.split("/");
                this.hqlFileNameList.add(splitFp[splitFp.length-1]);
            }
            else {
                List<Path> hqlFilePathList = new ArrayList<>();
                try (Stream<Path> walk = Files.walk(Paths.get(filePath))) {
                    hqlFilePathList = walk
                            .filter(Files::isRegularFile)
                            .filter(f -> f.getFileName().toString().endsWith(".hql"))
                            .collect(Collectors.toList());
                }
                Collections.sort(hqlFilePathList);
                for (Path pth : hqlFilePathList) {
                    fileBytesList.add(Files.readAllBytes(pth));
                    splitFp = pth.toString().split("/");
                    this.hqlFileNameList.add(splitFp[splitFp.length-1]);
                }
            }
        }
        catch (IOException e) {
            System.out.println("\n[ERROR]: Could not read .hql file. Please check the passed filepath.");
            e.printStackTrace();
            System.exit(1);
        }

        fileBytesList.forEach(fb -> this.hiveQLList.add(new String(fb, StandardCharsets.UTF_8)));
    }

    public void validateHQL() {
        String querySample = "";
        int queryNum = 1;
        System.out.println("[INFO]: Line numbers of syntax errors are relative to the beginning of each individual query. This count also ignores comment-only lines.");
        if (this.isDirectory)
            System.out.println(String.format("\n[INFO]: Opening files in %s folder for syntax validation.", this.filePath));
        try {
            for (int i = 0; i < this.hiveQLList.size(); ++i) {
                String[] queryArray = this.extractQueries(this.hiveQLList.get(i));
                querySample = "";
                queryNum = 1;
                System.out.println(String.format("\n[INFO]: Opening %s for syntax validation.", this.hqlFileNameList.get(i)));
                for (String query : queryArray) {
                    if (!query.trim().equals("")) {
                        querySample = query.substring(0, Math.min(query.length(), 75)).replaceAll("\\s{1,}", " ").trim();
                        System.out.println(String.format("\n[INFO]: Validating query #%d...\n[INFO]: %s...", queryNum, querySample));
                        this.parseDriver.parse(query);
                        System.out.println(String.format("[INFO]: Query #%d passed syntax validation.", queryNum, querySample));
                    }
                    else {
                        System.out.println(String.format("[INFO]: Query #%d is empty.", queryNum));
                    }
                    ++queryNum;
                }
                System.out.println(String.format("\n[INFO]: All queries in %s have passed HiveQL syntax validations.", this.hqlFileNameList.get(i)));
            }
        }
        catch (ParseException e) {
            System.out.println(String.format("\n[ERROR]: Error validating query #%d...\n[ERROR]: %s...", queryNum, querySample));
            System.out.println(String.format("[ERROR]: %s", e));
            System.exit(1);
        }
        if (this.isDirectory)
            System.out.println(String.format("\n[INFO]: All files in %s folder have passed HiveQL syntax validations.", this.filePath));
    }

    private String[] extractQueries(String hiveQL) {
        hiveQL = hiveQL.toLowerCase();
        // Could be refactored to use two patterns/function calls
        hiveQL = hiveQL.replaceAll(this.commentPattern, "").trim(); // Strip comments first to avoid unpredictable keyword presence
        hiveQL = hiveQL.replaceAll(this.usePattern, "").trim();
        hiveQL = hiveQL.replaceAll(this.setPattern, "").trim();
        hiveQL = hiveQL.replaceAll(this.addJarPattern, "").trim();
        hiveQL = hiveQL.replaceAll(this.tempFuncPattern, "").trim();
        hiveQL = hiveQL.replaceAll(this.dbNamePattern, "").trim();
        hiveQL = hiveQL.replaceAll(this.variablePattern, "placeholder").trim(); // Overlaps dbNamePattern -> Order unfortunately matters!
        return hiveQL.split(";");
    }

    public static void main(String[] args) {
        String pathArg = (args.length > 0) ? args[0] : System.getProperty("user.dir");
        int pathArgLen = pathArg.length();
        boolean isDir = Files.isDirectory(Paths.get(pathArg));
        
        if (!isDir && (pathArgLen < 5 || !pathArg.substring(pathArgLen-4, pathArgLen).equals(".hql"))) {
            System.out.println("\n[ERROR]: A directory path or .hql filepath must be passed to this program as the first argument.");
            System.exit(1);
        }

        HiveQLValidator hiveQlValidator = new HiveQLValidator(pathArg, isDir);
        hiveQlValidator.validateHQL();
    }
}
