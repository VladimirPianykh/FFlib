package com.futurefactory.defaults.ftr_attributes.daters;

import java.awt.Color;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import com.futurefactory.Dater;

public class EmptyDater<T>implements Dater<T>{
	public JComponent apply(T t,LocalDate u){
		JLabel l=new JLabel();
		l.setOpaque(true);
		l.setBackground(Color.DARK_GRAY);
		l.setBorder(BorderFactory.createTitledBorder(null,u.toString(),TitledBorder.TOP,0,null,u.isEqual(LocalDate.now())?Color.GREEN:Color.WHITE));
		return l;
	}
}
