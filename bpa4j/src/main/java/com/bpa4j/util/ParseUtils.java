package com.bpa4j.util;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParseUtils{
	public static interface Skipper{
		public int adjustIndex(final int index,CharSequence s);
	}
	public static enum StandardSkipper implements Skipper{
		SYNTAX{
			public int adjustIndex(int index,CharSequence s){
				do{
					if(s.charAt(index)=='/'&&index!=0&&s.charAt(index-1)=='/')do ++index;while(s.charAt(index)!='\n');
					else if(s.charAt(index)=='*'&&index!=0&&s.charAt(index-1)=='/'){
						++index;
						do ++index;while(!(s.charAt(index-1)=='*'&&s.charAt(index)=='/'));
					}else if(s.charAt(index)=='"')do{if(s.charAt(index)=='\\')++index;++index;}while(s.charAt(index)!='"');
					else if(s.charAt(index)=='\'')do{if(s.charAt(index)=='\\')++index;++index;}while(s.charAt(index)!='\'');
					else return index;
					++index;
				}while(s.charAt(index)=='"'||s.charAt(index)=='\'');
				return index;
			}
		},
		OUTERSCOPE{
			@SuppressWarnings("PMD.TooFewBranchesForASwitchStatement")
			public int adjustIndex(int index,CharSequence s){
				index=SYNTAX.adjustIndex(index,s);
				if(index==0)return index;
				switch(s.charAt(index-1)){
					case '(','[','{':
					int k=1;
					while(true){
						switch(s.charAt(index)){
							case '(','[','{'->++k;
							case ')',']','}'->--k;
						}
						if(k==0)break;
						++index;
					}
				}
				return index;
			}
		}
	}
	private ParseUtils(){}
	public static final Pattern classDefPattern=Pattern.compile("(\\w+)(?: (?:extends|implements) (.*?)\\s*){0,2}\\s*\\{");
	public static Pattern createSubClassPattern(String superClassPattern){
		return Pattern.compile("(?:class|interface|enum) (\\w+).*?(?: (?:extends|implements) .*?"+superClassPattern+").*\\{");
	}
	/**
	 * Delegates to the {@link #findFirstBlock(Pattern, String, char, char, Skipper)} with {@code null} as skipper
	 */
	public static String findFirstBlock(Pattern signature,CharSequence s,char open,char close){
		return findFirstBlock(signature,s,open,close,null);
	}
	/**
	 * Finds the first block of {@code signature} and a balanced bracket sequence.
	 * @param signature - pattern to use
	 * @param s - the string to search in
	 * @param open - open bracket character
	 * @param close - closed bracket character
	 * @return a map where keys are {@code signatureFound.group(1)} and the values are bodies of the blocks (from the first open bracket to the last closed exclusively).
	 */
	public static String findFirstBlock(Pattern signature,CharSequence s,char open,char close,Skipper skipper){
		StringBuilder b=new StringBuilder();
		Matcher m=signature.matcher(s);
		if(!m.find())return null;
		int k=1;
		int i=m.end();
		while(s.charAt(i)!=open)++i;
		for(++i;i<s.length();++i){
			if(skipper!=null){
				int ii=skipper.adjustIndex(i,s);
				if(ii!=i){
					b.append(s.subSequence(i,ii));
					i=ii;
				}
			}
			char c=s.charAt(i);
			if(c==open)++k;
			else if(c==close)--k;
			if(k==0)break;
			b.append(c);
		}
		return b.toString();
	}
	/**
	 * Finds all balanced bracket sequences preceded by the {@code signature} pattern and captures their names.
	 * @param signature - pattern to use
	 * @param s - the string to search in
	 * @param open - open bracket character
	 * @param close - closed bracket character
	 * @return a map where keys are {@code signatureFound.group(1)} and the values are bodies of the blocks (from the first open bracket to the last closed exclusively).
	 */
	public static TreeMap<String,String>findAllBlocks(Pattern signature,CharSequence s,char open,char close,Skipper skipper){
		TreeMap<String,String>a=new TreeMap<>();
		StringBuilder b=new StringBuilder();
		Matcher m=signature.matcher(s);
		int i=0;
		while(m.find(i)){
			int k=1;
			i=m.end();
			while(s.charAt(i)!=open)++i;
			for(++i;i<s.length();++i){
				if(skipper!=null){
					int ii=skipper.adjustIndex(i,s);
					if(ii!=i){
						b.append(s.subSequence(i,ii));
						i=ii;
					}
				}
				char c=s.charAt(i);
				if(c==open)++k;
				else if(c==close)--k;
				if(k==0)break;
				b.append(c);
			}
			a.put(m.group(1),b.toString());
			b.setLength(0);
		}
		return a;
	}
	public static int indexOf(CharSequence s,Pattern p){
		Matcher m=p.matcher(s);
		if(m.find())return m.start();
		return -1;
	}
	public static int indexOf(CharSequence s,Pattern p,int fromIndex){
		Matcher m=p.matcher(s);
		if(m.find(fromIndex))return m.start();
		return -1;
	}
	/**
	 * Finds the first pattern occurence in the given string.
	 * @param skipper - the skipper to use (can be null)
	 * @return the first index of successfull match.
	 */
	public static int indexOf(CharSequence s,Pattern p,Skipper skipper){return indexOf(s,p,skipper,0);}
	/**
	 * Finds the first pattern occurence in the given string after or at the specified index.
	 * @param skipper - the skipper to use (can be null)
	 * @return the first index of successfull match.
	 */
	public static int indexOf(CharSequence s,Pattern p,Skipper skipper,int fromIndex){
		StringBuilder b=new StringBuilder();
		int i=fromIndex;
		for(;i<s.length();++i){
			int ii=skipper.adjustIndex(i,s);
			if(ii!=i){
				Matcher m=p.matcher(b);
				if(m.find())return m.start()+(i-b.length());
				b.setLength(0);
				i=ii;
			}
			b.append(s.charAt(i));
		}
		Matcher m=p.matcher(b);
		if(m.find())return m.start()+(i-b.length());
		return -1;
	}
	/**
	 * Rewrites the string without the text ignored by skippers
	 * @param skipper - the skipper to use (can be null)
	 */
	public static String rewrite(CharSequence s,Skipper skipper){
		StringBuilder b=new StringBuilder();
		for(int i=0;i<s.length();++i){
			i=skipper.adjustIndex(i,s);
			b.append(s.charAt(i));
		}
		return b.toString();
	}
}
