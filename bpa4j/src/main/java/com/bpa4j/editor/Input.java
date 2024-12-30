package com.bpa4j.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Input{
    Class<? extends Verifier>verifier()default Verifier.class;
    Class<? extends NameProvider>nameProvider()default NameProvider.class;
    Class<? extends InfoProvider>infoProvider()default InfoProvider.class;
}
