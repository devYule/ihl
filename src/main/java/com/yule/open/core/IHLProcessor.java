package com.yule.open.core;

import com.google.auto.service.AutoService;
import com.yule.open.annotations.EnableEntityGenerator;
import com.yule.open.core.chain.Chain;
import com.yule.open.core.chain.impl.*;
import com.yule.open.core.context.ProcessContext;
import com.yule.open.core.context.impl.DefaultProcessContext;

import com.yule.open.properties.enums.EnvironmentProperties;
import com.yule.open.utils.NameGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.*;

@AutoService(Processor.class)
public class IHLProcessor extends AbstractProcessor {

    public final static NameGenerator embeddedEntityNameGenerator;
    public final static ProcessContext context;

    static {
        embeddedEntityNameGenerator = new NameGenerator("Embedded", "Id");
        context = new DefaultProcessContext();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(EnableEntityGenerator.class.getName());
        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> set = new HashSet<>();
        set.add("db.url");
        set.add("db.username");
        set.add("db.password");
        set.add("db.schema");
        set.add("entity.path");
        set.add("project.root");
        set.add("need.getter");
        set.add("need.setter");
        set.add("need.noArgs");
        set.add("need.allArgs");
        set.add("need.builder");
        set.add("entity.name.prefix");
        set.add("entity.name.suffix");
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        context.addContext(ProcessingEnvironment.class, processingEnv);
        context.addContext(RoundEnvironment.class, roundEnv);

        return Chain.build(
                new Initializr(1),
                new ElementValidator(2, EnableEntityGenerator.class),
                new EnvironmentResolver(3),
                new NameGeneratorGenerator(4),
                new ConnectionAnalyser(5),
                new TableFinder(6),
                new EntityFinder(7),
                new EntityTableFilter(8),
                new EntityTableAnalyser(9),
                new SourceGenerator(10),
                new ConnectionCloser(11)
        ).execute();
    }
}
