package com.futurefactory.editor;

import com.futurefactory.core.Data.Editable;

public interface Verifier{
    public String verify(Editable original,Editable e,boolean isNew);
}
