package com.iaai.onyard.classes;

/**
 * Class that represents the selection clause of a database query.
 */
public class SelectionClause {
	
	/**
	 * The selection clause with ? as a replaceable parameter for selection arguments.
	 */
	private String mSelection;
	/**
	 * String array containing the selection arguments to replace the ?s in the String above.
	 */
	private String[] mSelectionArgs;
	
	/**
	 * Constructor that initializes the selection string and argument array.
	 * 
	 * @param selection The selection clause with ? in place of arguments.
	 * @param selectionArgs The array of selection arguments.
	 */
	public SelectionClause(String selection, String[] selectionArgs)
	{
		if(selection == null)
		{
			setSelection("");
		}
		else
		{
			setSelection(selection);
		}
		
		if(selectionArgs == null)
		{
			setSelectionArgs(new String[0]);
		}
		else
		{
			setSelectionArgs(selectionArgs);
		}
	}
	
	/**
	 * Default constructor.
	 */
	public SelectionClause()
	{
		setSelection("");
		setSelectionArgs(new String[0]);
	}

	public void setSelection(String selection) {
		if(selection != "" && selection.charAt(0) == '(' && 
				selection.charAt(selection.length() - 1) == ')')
		{
			this.mSelection = selection;
		}
		else
		{
			this.mSelection = "(" + selection + ")";
		}
	}

	public String getSelection() {
		return mSelection;
	}

	public void setSelectionArgs(String[] selectionArgs) {
		this.mSelectionArgs = selectionArgs;
	}

	public String[] getSelectionArgs() {
		return mSelectionArgs;
	}
	
	/**
	 * Append the selection clause and selection arguments of the given SelectionClause
	 * object to those of this object. Each selection clause will be surrounded by parentheses
	 * and the given operator will be used to link them.
	 * 
	 * @param appendOperator The operator used to combine the two selection clauses into one.
	 * @param clauseAppend The SelectionClause object which should have its selection clause and
	 * arguments appended to this object's clause and arguments.
	 */
	public void append(String appendOperator, SelectionClause clauseAppend)
	{
		if(this.isEmpty())
		{
			setSelection(clauseAppend.getSelection());
			setSelectionArgs(clauseAppend.getSelectionArgs());
		}
		else
		{
			mSelection = mSelection + " " + appendOperator + " " + clauseAppend.getSelection();
			mSelectionArgs = arrayConcat(mSelectionArgs, clauseAppend.getSelectionArgs());
		}
	}
	
	/**
	 * Concatenate two string arrays and return the result.
	 * 
	 * @param arrayA The string array which should have its values copied starting at the 0 index
	 * of the new array.
	 * @param arrayB The string array which should be joined to the end of array A.
	 * @return A new array that contains the values of array A followed by the values of array B.
	 */
	private String[] arrayConcat(String[] arrayA, String[] arrayB)
	{
		int aLen = arrayA.length;
		int bLen = arrayB.length;
		String[] newArray = new String[aLen + bLen];
		
		System.arraycopy(arrayA, 0, newArray, 0, aLen);
		System.arraycopy(arrayB, 0, newArray, aLen, bLen);

		return newArray;
	}
	
	/**
	 * Check if the current object's member variables have not yet been set to valid values.
	 * 
	 * @return True if the members have not been set, false otherwise.
	 */
	private boolean isEmpty()
	{
		return mSelection.equals("()") || mSelectionArgs.length == 0;
	}
}
