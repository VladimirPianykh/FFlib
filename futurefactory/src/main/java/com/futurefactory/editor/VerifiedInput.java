package com.futurefactory.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VerifiedInput{
    Class<? extends Verifier>verifier();
}
