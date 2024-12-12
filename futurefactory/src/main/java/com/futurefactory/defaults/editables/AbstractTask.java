package com.futurefactory.defaults.editables;

import java.time.LocalDate;

import com.futurefactory.Data.Editable;
import com.futurefactory.editor.EditorEntry;

/**
 * A {@link Task} with creation date, deadline and additional info fields provided.
 */
public abstract class AbstractTask extends Editable{
    @EditorEntry(translation="Время создания")
    public LocalDate creationDate=LocalDate.now();
    @EditorEntry(translation="Дедлайн")
    public LocalDate deadline=LocalDate.now().plusDays(3);
    @EditorEntry(translation="Доп. информация")
    public String additional="";
    public AbstractTask(){super("Задание #"+(int)(Math.random()*1000000));}
}
