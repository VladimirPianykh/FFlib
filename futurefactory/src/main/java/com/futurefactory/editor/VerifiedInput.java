package com.futurefactory.editor;

public @interface VerifiedInput{
    Class<? extends Verifier> verifier();
}
