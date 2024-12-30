package com.bpa4j.editor;

import com.bpa4j.core.Data.Editable;

public interface Verifier{
    public String verify(Editable original,Editable e,boolean isNew);
}
