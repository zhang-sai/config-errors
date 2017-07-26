package edu.washington.cs.conf.mutation;

import java.io.File;

import edu.washington.cs.conf.util.Utils;

//check if the error message is adaquate
public class MessageAnalyzer {
	

	//1. check if the error messages contain the text or not
	//2. check if the error messages have consistent meanings with the manuals
	//   - if the error message if far away from similar, then discard
	//   - if multiple manual messages are similar with the error message
	
	public static MessageAdequacy isMessageAdequate(ExecResult result, UserManual manual) {
		String mutatedConfigOption = result.getMutatedOption();
		String mutatedValue = result.getMutatedValue();
		String errorMsg = result.getMessage();
		
		Utils.checkNotNull(errorMsg);
		
		MessageAdequacy msgAdequacy = new MessageAdequacy();
		
		boolean containOptionName = TextAnalyzer.containsOptionName(errorMsg, mutatedConfigOption);
		boolean containOptionValue = TextAnalyzer.containsOptionValue(errorMsg, mutatedValue);
		
		//if it contains name and option value, just return
		if(containOptionName || containOptionValue) {
			msgAdequacy.setContainOptionName(containOptionName);
			msgAdequacy.setContainOptionValue(containOptionValue);
			return msgAdequacy;
		}
		
		//check its similarity
		boolean closeEnoughToManual = TextAnalyzer.isMessageCloseEnough(errorMsg,
				manual.getDescription(result.getMutatedOption()), manual);
		boolean closestInManual = TextAnalyzer.isClosestInManual(errorMsg, result.getMutatedOption(), manual);
		msgAdequacy.setCloseEnoughToUserManual(closeEnoughToManual);
		msgAdequacy.setClosestToAllUserDesc(closestInManual);
		
		return msgAdequacy;
	}

}