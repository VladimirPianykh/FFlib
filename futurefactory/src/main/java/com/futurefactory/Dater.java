package com.futurefactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.function.BiFunction;

import javax.swing.JComponent;

public interface Dater<T>extends BiFunction<T,LocalDate,JComponent>,Serializable{
    
}
