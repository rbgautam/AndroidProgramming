package com.iaai.onyard.classes;

import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.SelectionClause;
import com.iaai.onyardproviderapi.contract.OnYardContract;

public class SearchHelper {
    
    private boolean mIsMultipleWordQuery;
    private SelectionClause mSearchSelection;
    
    public SearchHelper(String searchQuery)
    {
        mIsMultipleWordQuery = searchQuery.trim().contains(" ");
        mSearchSelection = getVehicleSearchSelection(searchQuery);
    }
    
    public SelectionClause getVehicleSearchSelection()
    {
        return mSearchSelection;
    }

    private SelectionClause getVehicleSearchSelection(String searchQuery)
    {
        SelectionClause selection = new SelectionClause();
        searchQuery = searchQuery.trim();

        if(searchQuery.contains(" "))
        {
            selection.append("AND", getVehicleSearchSelection(
                    searchQuery.substring(0, searchQuery.indexOf(" "))));
            selection.append("AND", getVehicleSearchSelection(
                    searchQuery.substring(searchQuery.indexOf(" "), searchQuery.length())));
            return selection;
        }
        else
        {
            selection = getSingleWordSelection(searchQuery);
            return selection;
        }
    }

    private SelectionClause getSingleWordSelection(String searchWord)
    {
        final SelectionClause clause = new SelectionClause();
        final StringBuilder selection = new StringBuilder();
        final int searchWordLength = searchWord.length();

        if (DataHelper.isWordOnlyNumeric(searchWord))
        {
            if (mIsMultipleWordQuery && searchWordLength == 2)
            {
                //search for year last 2 EXACT or model LIKE or claim # EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_YEAR).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                
                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{"%" + searchWord, "%" + searchWord + "%", searchWord});

            }
            else if (mIsMultipleWordQuery && searchWordLength == 4)
            {
                //search for year EXACT or model EXACT or claim # EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_YEAR).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");

                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{searchWord, searchWord, searchWord});
            }
            else if (searchWordLength == 6)
            {
                if(mIsMultipleWordQuery)
                {
                    //search for VIN last 6 EXACT or model EXACT or claim # EXACT
                    selection.append(OnYardContract.Vehicles.COLUMN_NAME_VIN).append(" LIKE ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                    
                    clause.setSelection(selection.toString());
                    clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord, searchWord});
                }
                else
                {
                    //search for VIN last 6 EXACT or model EXACT or claim # EXACT or stock # LIKE
                    selection.append(OnYardContract.Vehicles.COLUMN_NAME_VIN).append(" LIKE ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" = ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER).append(" LIKE ?");
                    
                    clause.setSelection(selection.toString());
                    clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord, searchWord, "%" + searchWord + "%"});
                }
            }
            else
            {
                if(mIsMultipleWordQuery)
                {
                    //search for model LIKE or claim # EXACT
                    selection.append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
    
                    clause.setSelection(selection.toString());
                    clause.setSelectionArgs(new String[]{"%" + searchWord + "%", searchWord});
                }
                else
                {
                    //search for model LIKE or claim # EXACT or stock # LIKE
                    selection.append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                    selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER).append(" LIKE ?");
    
                    clause.setSelection(selection.toString());
                    clause.setSelectionArgs(new String[]{"%" + searchWord + "%", searchWord, "%" + searchWord + "%"});
                }
            }
        }
        else if (DataHelper.isWordPartlyNumeric(searchWord))
        {
            if (searchWordLength == 17)
            {
                //search for VIN EXACT or claim # EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_VIN).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");

                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{searchWord, searchWord});
            }
            else if (searchWordLength == 6)
            {
                //search for VIN last 6 EXACT or claim # EXACT or model LIKE or make EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_VIN).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MAKE).append(" = ?");

                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{"%" + searchWord, searchWord,
                        "%" + searchWord + "%", searchWord});
            }
            else if (searchWordLength == 12)
            {
                //search for claim # EXACT or model LIKE or make EXACT or stock # EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MAKE).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER).append(" = ?");

                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{searchWord, "%" + searchWord + "%", searchWord,
                        searchWord});
            }
            else
            {
                //search for claim # EXACT or model LIKE or make EXACT
                selection.append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
                selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MAKE).append(" = ?");

                clause.setSelection(selection.toString());
                clause.setSelectionArgs(new String[]{searchWord, "%" + searchWord + "%", searchWord});
            }
        }
        else
        {
            //search for make LIKE or model LIKE or claim # EXACT
            selection.append(OnYardContract.Vehicles.COLUMN_NAME_MAKE).append(" LIKE ?");
            selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_MODEL).append(" LIKE ?");
            selection.append(" OR ").append(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER).append(" = ?");

            clause.setSelection(selection.toString());
            clause.setSelectionArgs(new String[]{"%" + searchWord + "%", "%" + searchWord + "%",
                    searchWord});
        }

        return clause;
    }
}
