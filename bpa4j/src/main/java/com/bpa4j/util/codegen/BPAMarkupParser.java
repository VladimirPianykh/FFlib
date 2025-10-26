package com.bpa4j.util.codegen;

import java.io.File;

import com.bpa4j.util.codegen.legacy.ProjectGraphLegacy;

/**
 * An utility class for parsing {@code .bpamarkup} files.
 */
public final class BPAMarkupParser{
	/**
	 * @deprecated Uses the old {@link ProjectGraphLegacy ProjectGraph} system.
	 * Parses a markup file into the designated project
	 */
	public static void parse(File markup,ProjectGraphLegacy graph){
		
	}
	
	/**
	 * Parses a markup file into the designated project (V2)
	 */
	public static void parse(File markup,ProjectGraph graph){
		//TODO: parse BPAMarkup
	}
}
