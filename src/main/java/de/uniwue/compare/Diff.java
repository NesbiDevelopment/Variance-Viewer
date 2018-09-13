package de.uniwue.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.parser.ParseException;

import de.uniwue.compare.token.TextToken;
import de.uniwue.compare.token.Token;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

/**
 * Implementation of ChangeAlgorithm with google-diff-match-patch
 * 
 */
public class Diff {
	final static String TEI = "de.uniwue.kalimachos.coref.type.TeiType";

	public static List<ConnectedContent> compareXML(String content1Text, String content2Text,
			Collection<Annotation> annotations1, Collection<Annotation> annotations2, Settings normalizerStorage)
			throws ParseException {

		final Comparator<Annotation> annotationComparator = (a1, a2) -> {
			int compare = Long.compare(a1.getBegin(), a2.getBegin());
			return (compare != 0) ? compare : Long.compare(a1.getEnd(), a2.getEnd());
		};

		final List<Annotation> sortedAnnotations1 = new ArrayList<>(annotations1);
		sortedAnnotations1.sort(annotationComparator);
		final List<Annotation> sortedAnnotations2 = new ArrayList<>(annotations2);
		sortedAnnotations2.sort(annotationComparator);

		
		// Compute diff. Get the Patch object.
		final List<Token> tokens1 = Tokenizer.tokenize(content1Text, sortedAnnotations1, normalizerStorage.getContentTags());
		final List<Token> tokens2 = Tokenizer.tokenize(content2Text, sortedAnnotations2, normalizerStorage.getContentTags());
		
		final List<TextToken> textTokens1 = tokens1.stream().map(t -> t.getTextToken()).collect(Collectors.toList());
		final List<TextToken> textTokens2 = tokens2.stream().map(t -> t.getTextToken()).collect(Collectors.toList());
		
		// Text compare
		final Patch<TextToken> textPatch = DiffUtils.diff(textTokens1, textTokens2);
		final List<Delta<TextToken>> textDeltas = textPatch.getDeltas();
		for(Delta<TextToken> text : textDeltas) {
			if(text.getOriginal().getLines().size() > 0 && text.getOriginal().getLines().get(0).getBegin() < 1000)
				System.out.println(text.getOriginal().getLines().get(0).getBegin());
			if(text.getRevised().getLines().size() > 0 && text.getRevised().getLines().get(0).getBegin() < 1000)
				System.out.println(text.getRevised().getLines().get(0).getBegin());
		
		}
		for(ConnectedContent content : DiffCreator.patch(textTokens1, textTokens2, textDeltas, true, normalizerStorage)) {
			if(content.getOriginal() != null && content.getOriginal().size() > 0 && content.getOriginal().get(0).getBegin() < 1000)
				System.out.println(content.getOriginal().get(0).getBegin());

			if(content.getRevised() != null && content.getRevised().size() > 0 && content.getRevised().get(0).getBegin() < 1000)
				System.out.println(content.getRevised().get(0).getBegin());
		}
		

		return DiffCreator.patch(textTokens1, textTokens2, textDeltas, true, normalizerStorage);
	}

	public static List<ConnectedContent> comparePlainText(String content1, String content2, Settings normalizerStorage)
			throws PatchFailedException {
		List<Token> tokens1 = Tokenizer.tokenize(content1);
		List<Token> tokens2 = Tokenizer.tokenize(content2);

		// Compute diff. Get the Patch object.
		Patch<Token> patch = DiffUtils.diff(tokens1, tokens2);

		return DiffCreator.patch(tokens1, tokens2, patch.getDeltas(), false, normalizerStorage);
	}
}