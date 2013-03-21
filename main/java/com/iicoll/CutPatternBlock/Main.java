package com.iicoll.CutPatternBlock;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import  com.iicoll.CutPatternBlock.CutPatternBlockM;

public class Main {
	public static void main(String[] args) throws IOException,
	ParserConfigurationException {

if (args.length > 2) {
	CutPatternBlockM.Start(args);
} else {
	System.out
			.println("at least 3 arguments required on input: (1) the file or dir to be processed (with the path to it), (2) output dir,(3) xml tag name, [(4) its expected value]");
}

}


public Main() {
super();
}

}
