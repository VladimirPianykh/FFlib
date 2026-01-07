package com.bpa4j.util.codegen;

import java.util.ArrayList;

interface DiagnosticService{
	ArrayList<Problem> findProblems(ProjectGraph graph);
}