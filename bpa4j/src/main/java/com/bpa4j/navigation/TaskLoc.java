package com.bpa4j.navigation;

import java.util.List;

public interface TaskLoc{
    default List<ImplementedInfo>getImplementedInfo(){return List.of();}
}
