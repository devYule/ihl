package com.yule.open.utils.javapoet.spec.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.yule.open.database.data.AnalyseResult;
import com.yule.open.database.data.Column;
import com.yule.open.database.data.Constraint;
import com.yule.open.database.data.Node;
import com.yule.open.database.data.Table;
import com.yule.open.properties.Environment;
import com.yule.open.utils.SourceGenerator;
import com.yule.open.utils.javapoet.spec.JavaPoetSpecGenerateCommander;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.yule.open.properties.enums.EnvironmentProperties.*;
import static com.yule.open.properties.enums.EnvironmentProperties.Required.PROJECT_ROOT;
import static com.yule.open.utils.Logger.error;

public class JavapoetNodeBatchSourceGenerator<T extends AnalyseResult, R extends TypeSpec> implements SourceGenerator<T, R> {


    private List<List<Integer>> graph;
    private List<Node> node;

    private JavaPoetSpecGenerateCommander specGenerator;


    @Override
    public List<R> generate(T info) {
        this.graph = info.getGraph();
        this.node = info.getNode();

        this.specGenerator = new JavaPoetSpecGenerateCommander(info.getNode().size());


        /* DFS, use specGenerator */
        dfs(0);
        /* DFS end */

        List<TypeSpec> ready = specGenerator.build();
        for (TypeSpec typeSpec : ready) {
            write(typeSpec);
        }
        return (List<R>) ready;

    }

    private void write(TypeSpec spec) {
        String projectRoot = Environment.get(PROJECT_ROOT);
        String outputDir = Paths.get(projectRoot, "src/main/java").toString();
        File dir = new File(outputDir);
        Path path = dir.toPath();
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                error("Generate directories error!");
                throw new RuntimeException(e);
            }
        }
        try {
            JavaFile.builder(Environment.get(Required.ENTITY_PATH), spec)
                    .build()
                    .writeTo(new File(outputDir));
        } catch (IOException e) {
            error("Write file error!");
            throw new RuntimeException(e);
        }
    }


    private void dfs(int nodeNum) {
        List<Integer> nums = graph.get(nodeNum);

        for (Integer num : nums) {
            Node n = node.get(num);

            if (n instanceof Table) {
                specGenerator.generate((Table) n, num);
            }
            if (n instanceof Column) {
                specGenerator.generate((Column) n, num, nodeNum);
            }
            if (n instanceof Constraint) {
                specGenerator.generate((Constraint) n, nodeNum);
            }
            dfs(num);

        }
    }
}
