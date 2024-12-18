package com.futurefactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.function.BiFunction;

import javax.swing.JComponent;

/**
 * Just a {@link Serializable} {@link BiFunction}, taking {@link LocalDate} (as a second argument) and returning {@link JComponent}.
 */
public interface Dater<T>extends BiFunction<T,LocalDate,JComponent>,Serializable{
    
}
