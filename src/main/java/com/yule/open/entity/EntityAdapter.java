package com.yule.open.entity;


import javax.lang.model.element.Element;
import java.util.List;


public interface EntityAdapter {

    int resolveEntityPath();

    boolean hasEntityAnnotation(Element el);

    List<String> getAlreadyEntityNames();

    String getJPADependencyPath();

}
