package org.aksw.sessa.main;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.aksw.sessa.helper.files.handler.FileHandlerInterface;
import org.aksw.sessa.helper.files.handler.TsvFileHandler;
import org.aksw.sessa.helper.graph.GraphInterface;
import org.aksw.sessa.helper.graph.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SESSATest {

	public static final String TSV_FILE = "src/test/resources/en_surface_forms_small.tsv";
	private SESSA sessa = new SESSA();
	private String question;
	private Set<String> answer;

	@Before
	public void initialize() throws IOException{
		FileHandlerInterface handler = new TsvFileHandler(TSV_FILE);
		sessa.loadFileToHashMapDictionary(handler);
	}

	@Test
	public void testAnswer_onEmpty() {
		question = "";
		answer = sessa.answer(question);
		Assert.assertThat(answer, is(nullValue()));
	}

	@Test
	public void testAnswer_onRunningExample() {
		question = "birthplace bill gates wife";
		answer = sessa.answer(question);
		Assert.assertThat(answer,hasItem("http://dbpedia.org/resource/Dallas"));
	}

	@Test
	public void testAnswer_onObamaExample() {
		question = "birthplace barack obama wife";
		answer = sessa.answer(question);
		Assert.assertThat(answer,hasItem("http://dbpedia.org/resource/Chicago"));
	}

// For now this doesn't work
//	@Test
//	public void testAnswer_SelfReferencing(){
//		question = "elton john spouse spouse";
//		answer = sessa.answer(question);
//		Assert.assertThat(answer,hasItem("http://dbpedia.org/resource/Elton_John"));
//	}

  /**
   * This tests on issue #11.
   */
	@Test
  public void testAnswer_onInterlinkingProblem(){
	  question = "music by elton john current production minskoff theatre";
    answer = sessa.answer(question);
    Assert.assertThat(answer,hasItem("http://dbpedia.org/resource/The_Lion_King_(musical)"));
  }

	@Test
	public void testGetGraphFor_TestColors(){
		question = "music by elton john current production minskoff theatre";
		GraphInterface graph = sessa.getGraphFor(question);
		HashMap<String, Node> nodes = new HashMap<>();
		for(Node node : graph.getNodes()){
			nodes.put(node.getContent().toString(), node);
		}
		String answer = "http://dbpedia.org/resource/The_Lion_King_(musical)";
		Node answerNode = nodes.get(answer);
		Assert.assertThat(answerNode.getExplanation(), equalTo(question.split(" ").length));
	}
	// TODO: create tests for other questions

	// TODO: create tests for accessibility to QueryProcessing & Co.
}
