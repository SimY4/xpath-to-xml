package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.action.Action;

public interface NavigatorSpi {

    boolean canHandle(Object o);

    <T> T process(T xml, Iterable<Action> exprs);

}
