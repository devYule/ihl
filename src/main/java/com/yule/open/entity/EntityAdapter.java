package com.yule.open.entity;


import com.yule.open.utils.NameGenerator;

import javax.lang.model.element.Element;
import java.util.List;


public interface EntityAdapter {

    int resolveEntityPath();

    boolean hasEntityAnnotation(Element el, NameGenerator nameGenerator);

    List<String> getAlreadyEntityNames();

    String getJPADependencyPath();

}
