package com.iaai.onyard.utility;

import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.SelectionClause;

public class SearchHelper {
	
    public static SelectionClause getVehicleSearchSelection(String searchVal)
    {
    	SelectionClause selection = new SelectionClause();
    	searchVal = searchVal.trim();
    	
    	if(searchVal.contains(" "))
    	{
    		selection.append("AND", getVehicleSearchSelection(
    				searchVal.substring(0, searchVal.indexOf(" "))));
    		selection.append("AND", getVehicleSearchSelection(
    				searchVal.substring(searchVal.indexOf(" "), searchVal.length())));
    		return selection;
    	}
    	else
    	{
    		selection = getSingleWordSelection(searchVal);
    		return selection;
    	}
    }
    
    private static SelectionClause getSingleWordSelection(String searchWord)
    {
    	SelectionClause clause = new SelectionClause();
    	StringBuilder selection = new StringBuilder();
    	
    	if (isWordOnlyNumeric(searchWord))
    	{
    		if (searchWord.length() == 4)
    		{
    			//search for year EXACT or model EXACT or claim # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_YEAR).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{searchWord, searchWord, searchWord});
    		}
    		else if (searchWord.length() == 6)
    		{
    			//search for VIN last 6 EXACT or model EXACT or claim # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_VIN).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord, searchWord});
    		}
    		else if (searchWord.length() == 7 || searchWord.length() == 8)
    		{
    			//search for stock # last 8 EXACT or model EXACT or claim # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord, searchWord});
    		}
    		else
    		{
    			//search for model LIKE or claim # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{"%" + searchWord + "%", searchWord});
    		}
    	}
    	else if (isWordPartlyNumeric(searchWord))
    	{
    		if (searchWord.length() == 17)
    		{
    			//search for VIN EXACT or claim # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_VIN).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{searchWord, searchWord});
    		}
    		else if (searchWord.length() == 6)
    		{
    			//search for VIN last 6 EXACT or claim # EXACT or model LIKE or make EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_VIN).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MAKE).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord, 
    					"%" + searchWord + "%", searchWord});
    		}
    		else if (searchWord.length() == 12)
    		{
    			//search for claim # EXACT or model LIKE or make EXACT or stock # EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MAKE).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{searchWord, "%" + searchWord + "%", searchWord, 
    					searchWord});
    		}
    		else
    		{
    			//search for claim # EXACT or model LIKE or make EXACT
    			selection.append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
    			selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MAKE).append(" = ?");
    			
    			clause.setSelection(selection.toString());
    			clause.setSelectionArgs(new String[]{searchWord, "%" + searchWord + "%", searchWord});
    		}
    	}
    	else
    	{
    		//search for make LIKE or model LIKE or claim # EXACT
    		selection.append(OnYard.Vehicles.COLUMN_NAME_MAKE).append(" LIKE ?");
    		selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
    		selection.append(" OR ").append(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    		
    		clause.setSelection(selection.toString());
			clause.setSelectionArgs(new String[]{"%" + searchWord + "%", "%" + searchWord + "%", 
					searchWord});
    	}
    	
    	return clause;
    }
    
    private static boolean isWordOnlyNumeric(String word) 
    {
    	return getNumDigitChars(word) == word.length();
    }
    
    private static boolean isWordPartlyNumeric(String word) 
    {
    	return getNumDigitChars(word) > 0;
    }
    
    private static int getNumDigitChars(String string)
    {
    	int numNumbers = 0;
    	
    	for (int charIndex = 0, strLen = string.length(); charIndex < strLen; charIndex++) 
    	{
    	    if (Character.isDigit(string.charAt(charIndex))) 
    	    {
    	    	numNumbers++;
    	    }
    	}
    	
    	return numNumbers;
    }

}
