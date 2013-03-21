package com.iicoll.CutPatternBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;

import java.nio.charset.Charset;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CutPatternBlockM {

	private static String SchemaInst = "";

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());

			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	private static void getParentOnOutput(Node ParentNode, String OutFileName,
			String encoding, NamedNodeMap rootAttr) {
		String root = SchemaInst;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder;

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement(root);

			for (int i = 0; i < rootAttr.getLength(); i++) {
				rootElement.setAttribute(rootAttr.item(i).getNodeName(),
						rootAttr.item(i).getNodeValue());
			}
			document.appendChild(rootElement);
			Element em;

			Node CurrNode = ParentNode;
			String nname = CurrNode.getNodeName();
			List<String> PNList = new LinkedList<String>();
			;

			while (CurrNode.getParentNode() != null) {
				CurrNode = CurrNode.getParentNode();
				nname = CurrNode.getNodeName();
				PNList.add(nname);
			}
			// to exclude 1st element duplication, check whether it always
			// requires double remove
			PNList.remove(PNList.size() - 1);
			PNList.remove(PNList.size() - 1);

			ListIterator li = PNList.listIterator(PNList.size());

			// Iterate in reverse.
			while (li.hasPrevious()) {
				em = document.createElement((String) li.previous());
				rootElement.appendChild(em);
				rootElement = em;
			}

			em = document.createElement(ParentNode.getNodeName());
			rootElement.appendChild(em);
			rootElement = em;

			String element;
			String data;
			NodeList NL = ParentNode.getChildNodes();

			for (int i = 0; i < NL.getLength(); i++) {
				element = NL.item(i).getNodeName();
				if (element != null && element != "#text") {
					em = document.createElement(element);
					data = NL.item(i).getTextContent();
					em.appendChild(document.createTextNode(data));
					rootElement.appendChild(em);
				}
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				// set output encoding
				if (encoding == null) {
					encoding = "UTF-8";
				}
				transformer.setOutputProperty(OutputKeys.ENCODING, encoding);

				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(OutFileName);
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void ProcessFile(final File inFl, String tag,
			String content, String InFileName, DocumentBuilder db) {

		Document dom;
		String OutFileName;
		try {
			dom = db.parse(inFl);
			Element doc = dom.getDocumentElement();
			NamedNodeMap rootAttr = doc.getAttributes();
			String encoding = dom.getInputEncoding();
			Node fstElement = doc.getFirstChild();
			NamedNodeMap attr = doc.getAttributes();
			SchemaInst = doc.getTagName();
			NodeList fieldOfInterest = doc.getElementsByTagName(tag);
			for (int i = 0; i < fieldOfInterest.getLength(); i++) {
				if (content == null) {
					OutFileName = InFileName + "_" + i + ".xml";
					getParentOnOutput(fieldOfInterest.item(i).getParentNode(),
							OutFileName, encoding, rootAttr);
				} else if (fieldOfInterest.item(i).getTextContent()
						.toLowerCase().equals(content)) {
					// OutFileName is as input plus "_number"
					OutFileName = InFileName + "_" + i + ".xml";
					getParentOnOutput(fieldOfInterest.item(i).getParentNode(),
							OutFileName, encoding, rootAttr);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void Start(String[] args) throws IOException,
			ParserConfigurationException {

		if (args.length > 2) {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			final File folder = new File(args[0]);
			Filename FileN;
			String OutFileName;
			if (folder.isDirectory()) {
				for (final File fileEntry : folder.listFiles()) {
					FileN = new Filename(fileEntry.getName(), '/', '.');
					OutFileName = args[1] + '/' + FileN.filename();
					ProcessFile(fileEntry, args[2], args[3].toLowerCase(),
							OutFileName, db);
				}
			} else if (folder.isFile()) {
				FileN = new Filename(args[0], '/', '.');
				OutFileName = args[1] + '/' + FileN.filename();
				if (args.length == 3) {
					ProcessFile(folder, args[2], null, OutFileName, db);
				} else {
					ProcessFile(folder, args[2], args[3].toLowerCase(),
							OutFileName, db);
				}
			} else {
				System.out
						.println("1st argument has to be either file or folder");
			}
		} else {
			System.out
					.println("at least 3 arguments required on input: (1) the file or dir to be processed (with the path to it), (2) output dir,(3) xml tag name, [(4) its expected value]");
		}

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see java.lang.Object#Object()
	 */

}