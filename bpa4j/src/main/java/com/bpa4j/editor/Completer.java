package com.bpa4j.editor;

import com.bpa4j.core.Editable;

public interface Completer{
    public boolean isCompletable(Editable editable,int fieldsEdited);
    public void completeObject(Editable editable);
}
