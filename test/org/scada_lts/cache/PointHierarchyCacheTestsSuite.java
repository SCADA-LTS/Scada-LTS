package org.scada_lts.cache;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DeletePointHierarchyCacheTest.class,
        OnBaseParentIdPointHierarchyCacheTest.class,
        PointHierarchyCacheTest.class,
})
public class PointHierarchyCacheTestsSuite {
}
