package com.iaai.onyard.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.iaai.onyard.application.OnYard.ImageSet;
import com.iaai.onyardproviderapi.classes.BodyStyleSpecialtyInfo;
import com.iaai.onyardproviderapi.classes.BranchInfo;
import com.iaai.onyardproviderapi.classes.CheckinFieldInfo;
import com.iaai.onyardproviderapi.classes.CheckinTemplateInfo;
import com.iaai.onyardproviderapi.classes.ColorInfo;
import com.iaai.onyardproviderapi.classes.DamageInfo;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.DisabledEnhancementInfo;
import com.iaai.onyardproviderapi.classes.EnhancementInfo;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.iaai.onyardproviderapi.classes.ImageReshootInfo;
import com.iaai.onyardproviderapi.classes.ImageTypeInfo;
import com.iaai.onyardproviderapi.classes.LicensePlateConditionInfo;
import com.iaai.onyardproviderapi.classes.LossTypeInfo;
import com.iaai.onyardproviderapi.classes.OdometerReadingTypeInfo;
import com.iaai.onyardproviderapi.classes.PublicVinInfo;
import com.iaai.onyardproviderapi.classes.SaleDocTypeInfo;
import com.iaai.onyardproviderapi.classes.SalvageConditionInfo;
import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;
import com.iaai.onyardproviderapi.classes.SalvageProviderInfo;
import com.iaai.onyardproviderapi.classes.SalvageTypeInfo;
import com.iaai.onyardproviderapi.classes.SlaSalvageEnhancementInfo;
import com.iaai.onyardproviderapi.classes.StateInfo;
import com.iaai.onyardproviderapi.classes.StatusInfo;
import com.iaai.onyardproviderapi.classes.SyncWindowExceptionInfo;
import com.iaai.onyardproviderapi.classes.SyncWindowInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.iaai.onyardproviderimpl.provider.OnYardProvider;

public class OnYardProviderTest extends ProviderTestCase2<OnYardProvider> {

    private static final Uri INVALID_URI = Uri.withAppendedPath(
            OnYardContract.Vehicles.CONTENT_URI, "invalid");
    private MockContentResolver mMockResolver; 

    private final VehicleInfo[] TEST_VEHICLES = {
            
            new VehicleInfo("Stock0", "VIN0", "Claim0", 10f,  20f, "A", 30, "Col0", 40, "Make0", 
                    "Model0", 1, "St0", "D0", 0,  true, 0, false, 100l, 111, false, "L00", 10, 100, 1412928000, 151200000),
            new VehicleInfo("Stock1", "VIN1", "Claim1", 11f, 21f, "B", 31, "Col1", 41, "Make1",
                    "Model1", 2, "St1", "D1", 1, false, 1, true, 200, 111, false, "L01", 11, 101, 1412928000,144000000),
            new VehicleInfo("Stock2", "VIN2", "Claim2", 12, 22, "C", 32, "Col2", 42, "Make2",
                    "Model2", 3, "St2", "D2", 2, true, 2, false, 300, 111, false, "L02", 12, 102, 1412928000,151200000),
            new VehicleInfo("Stock3", "VIN3", "Claim3", 13, 23, "D", 33, "Col3", 43, "Make3",
                    "Model3", 4, "St3", "D3", 3, false, 3, true, 400, 111, true, "L03", 13, 103, 1412928000,144000000),
            new VehicleInfo("Stock4", "VIN4", "Claim4", 14, 24, "E", 34, "Col4", 44, "Make4",
                    "Model4", 5, "St4", "D4", 4, true, 4, true, 500, 111, false, "L04", 14, 104, 1412928000,151200000),
            new VehicleInfo("Stock5", "VIN5", "Claim5", 15, 25, "F", 35, "Col5", 45, "Make5",
                    "Model5", 6, "St5", "D5", 5, false, 5, false, 600, 111, false, "L05", 15, 105, 1412928000,15100000),
            new VehicleInfo("Stock6", "VIN6", "Claim6", 16, 26, "G", 36, "Col6", 46, "Make6",
                    "Model6", 7, "St6", "D6", 6, true, 6, false, 700, 111, true, "L06", 16, 106, 1412928000,13800000),
            new VehicleInfo("Stock7", "VIN7", "Claim7", 17, 27, "H", 37, "Col7", 47, "Make7",
                    "Model7", 8, "St7", "D7", 7, false, 7, false, 800, 111, false, "L07", 17, 107, 1412928000,151200000),
            new VehicleInfo("", "", "", 8, 8, "", 8, "", 8, "", "", 8, "", "", 8, false, 8, false,
                    8, 111, true, "L08", 18, 108, 1412928000, 144000000),
            new VehicleInfo("Stock8", null, null, 0, 0, null, 0, null, 0, null, null, 0, null,
                    null, 0, false, 0, false, 0, 111, false, "L09", 19, 109, 1412928000, 144000000) };
//    String stockNumber, String vin, String claimNumber, float latitude,
//    float longitude, String aisle, int stall, String color, int year, String make,
//    String model, int salvageProviderId, String status, String damage, int salvageType,
//    boolean hasImages, int saleDocTypeId, boolean isRunAndDrive, long auctionDate,
//    int adminBranch, boolean isDeleted, String lossTypeCode
    

    private final StatusInfo[] TEST_STATUS = {
            new StatusInfo("St0", "StDesc0"),
            new StatusInfo("St1", "StDesc1"),
            new StatusInfo("St2", "StDesc2"),
            new StatusInfo("St3", "StDesc3"),
            new StatusInfo("St4", "StDesc4")};
    
    private final ColorInfo[] TEST_COLOR = { new ColorInfo("Col0", "ColDesc0", false),
            new ColorInfo("Col1", "ColDesc1", false), new ColorInfo("Col2", "ColDesc2", false),
            new ColorInfo("Col3", "ColDesc3", false), new ColorInfo("Col4", "ColDesc4", false) };
    
    private final DamageInfo[] TEST_DAMAGE = { new DamageInfo("D0", "DDesc0", false),
            new DamageInfo("D1", "DDesc1", false), new DamageInfo("D2", "DDesc2", false),
            new DamageInfo("D3", "DDesc3", false), new DamageInfo("D4", "DDesc4", false) };
    
    private final SaleDocTypeInfo[] TEST_SALE_DOC_TYPE = { 
            new SaleDocTypeInfo(0, "SDesc0"),
            new SaleDocTypeInfo(1, "SDesc1"), 
            new SaleDocTypeInfo(2, "SDesc2"),
            new SaleDocTypeInfo(3, "SDesc3"), 
            new SaleDocTypeInfo(4, "SDesc4") };
    
    private final BranchInfo[] TEST_BRANCH = { 
            new BranchInfo("000", "Branch 000", "S0","CT", false),
            new BranchInfo("111", "Branch 111", "S1","ET", false),
            new BranchInfo("222", "Branch 222", "S2","PT", false),
            new BranchInfo("333", "Branch 333", "S3","PT", false),
            new BranchInfo("444", "Branch 444", "S4","AT", false)
    };

    private final LossTypeInfo[] TEST_LOSS_TYPE = { new LossTypeInfo("L00", "Loss Type 0", false),
            new LossTypeInfo("L11", "Loss Type 1", false),
            new LossTypeInfo("L22", "Loss Type 2", false),
            new LossTypeInfo("L33", "Loss Type 3", false),
            new LossTypeInfo("L44", "Loss Type 4", false)
            
    };

    private final BodyStyleSpecialtyInfo[] TEST_BODY_STYLE_SPECIALTY = { new BodyStyleSpecialtyInfo("70", "Body Style 0", false),
            new BodyStyleSpecialtyInfo("71", "Body Style 1", false),
            new BodyStyleSpecialtyInfo("72", "Body Style 2", false),
            new BodyStyleSpecialtyInfo("73", "Body Style 3", false),
            new BodyStyleSpecialtyInfo("74", "Body Style 4", false)
            
    };    

    private final LicensePlateConditionInfo[] TEST_LICENSE_PLATE_CONDITION = { new LicensePlateConditionInfo("L0", "Condition 0", false),
            new LicensePlateConditionInfo("L1", "Condition 1", false),
            new LicensePlateConditionInfo("L2", "Condition 2", false),
            new LicensePlateConditionInfo("L3", "Condition 3", false),
            new LicensePlateConditionInfo("L4", "Condition 4", false)
            
    };  
   
    private final OdometerReadingTypeInfo[] TEST_ODOMETER_READING_TYPE = { new OdometerReadingTypeInfo("40", "Reading Type 40", false),
            new OdometerReadingTypeInfo("41", "Reading Type 41", false),
            new OdometerReadingTypeInfo("42", "Reading Type 42", false),
            new OdometerReadingTypeInfo("43", "Reading Type 43", false),
            new OdometerReadingTypeInfo("44", "Reading Type 44", false)
            
    };    
    
    private final PublicVinInfo[] TEST_PUBLIC_VIN = { new PublicVinInfo("A", "VIN Status A", false),
            new PublicVinInfo("B", "VIN Status B", false),
            new PublicVinInfo("C", "VIN Status C", false),
            new PublicVinInfo("D", "VIN Status D", false),
            new PublicVinInfo("E", "VIN Status E", false)
            
    }; 
    
    private final SalvageConditionInfo[] TEST_SALVAGE_CONDITION = { new SalvageConditionInfo("SC0", "Salvage Condition 0", false),
            new SalvageConditionInfo("SC1", "Salvage Condition 1", false),
            new SalvageConditionInfo("SC2", "Salvage Condition 2", false),
            new SalvageConditionInfo("SC3", "Salvage Condition 3", false),
            new SalvageConditionInfo("SC4", "Salvage Condition 4", false)
            
    };  

    private final SalvageTypeInfo[] TEST_SALVAGE_TYPE = { new SalvageTypeInfo("STyp0", "Salvage 0", false),
            new SalvageTypeInfo("STyp1", "Salvage 1", false),
            new SalvageTypeInfo("STyp2", "Salvage 2", false),
            new SalvageTypeInfo("STyp3", "Salvage 3", false),
            new SalvageTypeInfo("STyp4", "Salvage 4", false)
    };

    private final CheckinFieldInfo[] TEST_CHECKIN_FIELD = {
            new CheckinFieldInfo(0, "field", "input", "caption", "code", null, false, null, null,
                    null, "member", false),
            new CheckinFieldInfo(1, "field1", "input1", "caption1", "c1", 1, true, 1, 1, 1,
                    "member1", true),
            new CheckinFieldInfo(2, "field2", "input2", "caption2", "c2", 2, false, 2, 2, 2,
                    "member2", true),
            new CheckinFieldInfo(3, "field3", "input3", "caption3", "c3", 3, true, 3, 3, 3,
                    "member3", false),
            new CheckinFieldInfo(4, "field4", "input4", "caption4", "c4", 4, false, 4, 4, 4,
                    "member4", false) };

    private final CheckinTemplateInfo[] TEST_CHECKIN_TEMPLATE = {
            new CheckinTemplateInfo("1", 0, 0, false),
            new CheckinTemplateInfo("2", 2, 1, false),
            new CheckinTemplateInfo("3", 1, 2, true),
            new CheckinTemplateInfo("1", 4, 3, true),
            new CheckinTemplateInfo("1", 3, 4, false), };
    
    private final StateInfo[] TEST_STATE = { new StateInfo("S0", "State  0", false),
            new StateInfo("S1", "State 1", false),
            new StateInfo("S2", "State 2", false),
            new StateInfo("S3", "State 3", false),
            new StateInfo("S4", "State 4", false)
            
    };    
    
    private final HashMap<String, String> TEST_CONFIG;
    {
        TEST_CONFIG = new HashMap<String, String>();
        TEST_CONFIG.put("Key1", "Value1");
        TEST_CONFIG.put("Key2", "Value2");
        TEST_CONFIG.put("Key3", "Value3");
        TEST_CONFIG.put("Key4", "Value4");
        TEST_CONFIG.put("Key5", "Value5");
    }

    private final ImageCaptionInfo[] TEST_IMAGE_CAPTION = {
            new ImageCaptionInfo(1, 1, 1, "Caption1", "FocusMode1", 640, 480, 0, "file1", false,
                    10, false, "file1"),
            new ImageCaptionInfo(2, 1, 1, "Caption2", "FocusMode1", 640, 480, 1, "file2", false,
                    20, true, "file2"),
            new ImageCaptionInfo(3, 1, 1, "Caption3", "FocusMode1", 640, 480, 2, "file3", false,
                    30, false, "file3"),
            new ImageCaptionInfo(4, 1, 1, "Caption4", "FocusMode1", 640, 480, 3, "file4", false,
                    40, true, "file4"),
            new ImageCaptionInfo(5, 1, 1, "Caption5", "FocusMode1", 640, 480, 4, "file5", false,
                    50, false, "file5"),
            new ImageCaptionInfo(1, 2, 1, "Caption1", "FocusMode1", 640, 480, 0, "file1", false,
                    60, true, "file6"),
            new ImageCaptionInfo(2, 2, 1, "Caption2", "FocusMode1", 640, 480, 1, "file2", false,
                    70, false, "file7"),
            new ImageCaptionInfo(3, 2, 1, "Caption3", "FocusMode1", 640, 480, 2, "file3", false,
                    80, true, "file8"),
            new ImageCaptionInfo(4, 2, 1, "Caption4", "FocusMode1", 640, 480, 3, "file4", false,
                    90, false, "file9"),
            new ImageCaptionInfo(5, 2, 1, "Caption5", "FocusMode1", 640, 480, 4, "file5", false,
                    100, true, "file10"),
            new ImageCaptionInfo(1, 3, 1, "Caption1", "FocusMode1", 640, 480, 0, "file1", false,
                    10, false, "file1"),
            new ImageCaptionInfo(2, 3, 1, "Caption2", "FocusMode1", 640, 480, 1, "file2", false,
                    20, true, "file2"),
            new ImageCaptionInfo(3, 3, 1, "Caption3", "FocusMode1", 640, 480, 2, "file3", false,
                    30, false, "file3"),
            new ImageCaptionInfo(4, 3, 1, "Caption4", "FocusMode1", 640, 480, 3, "file4", false,
                    40, true, "file4"),
            new ImageCaptionInfo(5, 3, 1, "Caption5", "FocusMode1", 640, 480, 4, "file5", false,
                    50, false, "file5") };
    
    private final DataPendingSync[] TEST_DATA_PENDING_SYNC = {
            new DataPendingSync(1, "Session1", "JsonName1", "ValueText1", null, null),
            new DataPendingSync(2, "Session2", "JsonName2", "ValueText2", null, null),
            new DataPendingSync(3, "Session3", "JsonName3", "ValueText3", null, null),
            new DataPendingSync(4, "Session4", "JsonName4", "ValueText4", null, null),
            new DataPendingSync(5, "Session5", "JsonName5", "ValueText5", null, null) };

    private final ImageReshootInfo[] TEST_IMAGE_RESHOOT = {
            new ImageReshootInfo("Stock1", 1, 1, false),
            new ImageReshootInfo("Stock02", 2, 0, true),
            new ImageReshootInfo("Stock02", 3, 1, false),
            new ImageReshootInfo("Stock03", 4, 0, true),
            new ImageReshootInfo("Stock4", 2, 1, false) };
    
    private final ImageTypeInfo[] TEST_IMAGE_TYPE = { new ImageTypeInfo(1, "ImageType1", false),
            new ImageTypeInfo(2, "ImageType2", false), new ImageTypeInfo(3, "ImageType3", false),
            new ImageTypeInfo(4, "ImageType4", false), new ImageTypeInfo(5, "ImageType5", false) };

    private final SalvageProviderInfo[] TEST_SALVAGE_PROVIDER = {
            new SalvageProviderInfo(1, "SProv0", false),
            new SalvageProviderInfo(2, "SProv1", true), new SalvageProviderInfo(3, "SProv2", true),
            new SalvageProviderInfo(4, "SProv3", false),
            new SalvageProviderInfo(5, "SProv4", false) };


    private final SlaSalvageEnhancementInfo[] TEST_SLA_SALVAGE_ENHANCEMENT = {
    		new SlaSalvageEnhancementInfo("000-11111111", 1, 111, true, true, false),
    		new SlaSalvageEnhancementInfo("000-22222222", 2, 222, true, false, false),
    		new SlaSalvageEnhancementInfo("000-33333333", 1, 333, false, true, false),
    		new SlaSalvageEnhancementInfo("000-44444444", 2, 444, false, false, false),
    };
    
    private final SalvageEnhancementInfo[] TEST_SALVAGE_ENHANCEMENT = {
    		new SalvageEnhancementInfo("000-11111111", 111, "AAA", 12345, false),
    		new SalvageEnhancementInfo("000-11111111", 222, "BBB", 12345, false)
    };
    
    private final EnhancementInfo[] TEST_ENHANCEMENT = {
    		new EnhancementInfo(11, "Enhancement 1", false),
    		new EnhancementInfo(22, "Enhancement 2", false),
    		new EnhancementInfo(33, "Enhancement 3", false)
    };
    
    private final DisabledEnhancementInfo[] TEST_DISABLED_ENHANCEMENT = {
    		new DisabledEnhancementInfo(11, false)
    };
    
    private final SyncWindowInfo [] TEST_SYNC_WINDOW = {
        	new SyncWindowInfo(2, 200, 60, false),
        	new SyncWindowInfo(3, 300, 60, false),
        	new SyncWindowInfo(4, 400, 60, false)
    };
    
    private final SyncWindowExceptionInfo[] TEST_SYNC_WINDOW_EXCEPTION = {
    		new SyncWindowExceptionInfo(111, false),
    		new SyncWindowExceptionInfo(222,false),
    		new SyncWindowExceptionInfo(333,false)
    };
    
    public OnYardProviderTest(Class<OnYardProvider> providerClass,
			String providerAuthority) {
        super(OnYardProvider.class, OnYardContract.AUTHORITY);
	}
	
	public OnYardProviderTest() {
        super(OnYardProvider.class, OnYardContract.AUTHORITY);
	}
	
    /*
     * Sets up the test environment before each test method. Creates a mock content resolver,
     * gets the provider under test, and creates a new database for the provider.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mMockResolver = getMockContentResolver();

        deleteTestData();
    }

    /*
     *  This method is called after each test method, to clean up the current fixture. Since
     *  the test case runs in an isolated context, no cleanup is necessary.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        deleteTestData();
    }

    private void deleteTestData() {
        mMockResolver.delete(OnYardContract.Vehicles.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Status.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Color.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Damage.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Config.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.ImageCaption.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.DataPendingSync.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SaleDocType.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.ImageReshoot.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.ImageType.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SalvageProvider.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Branch.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.LossType.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.BodyStyleSpecialty.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.LicensePlateCondition.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.OdometerReadingType.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.PublicVIN.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SalvageCondition.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SalvageType.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.State.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.CheckinField.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.CheckinTemplate.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SlaSalvageEnhancement.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SalvageEnhancement.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.Enhancement.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.DisabledEnhancement.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SyncWindow.CONTENT_URI, null, null);
        mMockResolver.delete(OnYardContract.SyncWindowException.CONTENT_URI, null, null);
    }
    
    /*
     * Sets up test data.
     * The test data is in an SQL database. It is created in setUp() without any data,
     * and populated in insertData if necessary.
     */
    private void insertData() {
        ContentValues[] vehicleValues = new ContentValues[TEST_VEHICLES.length];
        ContentValues[] colorValues = new ContentValues[TEST_COLOR.length];
        ContentValues[] statusValues = new ContentValues[TEST_STATUS.length];
        ContentValues[] damageValues = new ContentValues[TEST_DAMAGE.length];
        ContentValues[] saleDocTypeValues = new ContentValues[TEST_SALE_DOC_TYPE.length];
        ContentValues[] dataValues = new ContentValues[TEST_DATA_PENDING_SYNC.length];
        ContentValues[] typeValues = new ContentValues[TEST_IMAGE_TYPE.length];
        ContentValues[] salvageProviderValues = new ContentValues[TEST_SALVAGE_PROVIDER.length];
        ContentValues[] branchValues = new ContentValues[TEST_BRANCH.length];
        ContentValues[] lossTypeValues = new ContentValues[TEST_LOSS_TYPE.length];
        ContentValues[] bodyStyleSpecialtyValues = new ContentValues[TEST_BODY_STYLE_SPECIALTY.length];
        ContentValues[] licensePlateConditionValues = new ContentValues[TEST_LICENSE_PLATE_CONDITION.length];
        ContentValues[] odometerReadingTypeValues = new ContentValues[TEST_ODOMETER_READING_TYPE.length];
        ContentValues[] publicVinValues = new ContentValues[TEST_PUBLIC_VIN.length];
        ContentValues[] salvageConditionValues = new ContentValues[TEST_SALVAGE_CONDITION.length];
        ContentValues[] salvageTypeValues = new ContentValues[TEST_SALVAGE_TYPE.length];
        ContentValues[] stateValues = new ContentValues[TEST_STATE.length];
        ContentValues[] checkinFieldValues = new ContentValues[TEST_CHECKIN_FIELD.length];
        ContentValues[] checkinTemplateValues = new ContentValues[TEST_CHECKIN_TEMPLATE.length];
        ContentValues[] slaSalvageEnhancementValues = new ContentValues[TEST_SLA_SALVAGE_ENHANCEMENT.length];
        ContentValues[] salvageEnhancementValues = new ContentValues[TEST_SALVAGE_ENHANCEMENT.length];
        ContentValues[] enhancementValues = new ContentValues[TEST_ENHANCEMENT.length];
        ContentValues[] disabledEnhancementValues = new ContentValues[TEST_DISABLED_ENHANCEMENT.length];
        ContentValues[] syncWindowValues = new ContentValues[TEST_SYNC_WINDOW.length];
        ContentValues[] syncWindowExceptionValues = new ContentValues[TEST_SYNC_WINDOW_EXCEPTION.length];
        
        for (int index = 0; index < TEST_VEHICLES.length; index++) 
        {
        	vehicleValues[index] = TEST_VEHICLES[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Vehicles.CONTENT_URI, vehicleValues);
        
        for (int index = 0; index < TEST_STATUS.length; index++) 
        {
        	statusValues[index] = TEST_STATUS[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Status.CONTENT_URI, statusValues);
        
        for (int index = 0; index < TEST_COLOR.length; index++) 
        {
        	colorValues[index] = TEST_COLOR[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Color.CONTENT_URI, colorValues);
        
        for (int index = 0; index < TEST_DAMAGE.length; index++) 
        {
        	damageValues[index] = TEST_DAMAGE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Damage.CONTENT_URI, damageValues);

        for (int index = 0; index < TEST_SALE_DOC_TYPE.length; index++) {
            saleDocTypeValues[index] = TEST_SALE_DOC_TYPE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SaleDocType.CONTENT_URI, saleDocTypeValues);

        for (int index = 0; index < TEST_SALVAGE_PROVIDER.length; index++) {
            salvageProviderValues[index] = TEST_SALVAGE_PROVIDER[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SalvageProvider.CONTENT_URI, salvageProviderValues);

        for (int index = 0; index < TEST_IMAGE_TYPE.length; index++) 
        {
            typeValues[index] = TEST_IMAGE_TYPE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.ImageType.CONTENT_URI, typeValues);
        
        for (int index = 0; index < TEST_DATA_PENDING_SYNC.length; index++) {
            dataValues[index] = TEST_DATA_PENDING_SYNC[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.DataPendingSync.CONTENT_URI, dataValues);

        for (int index = 0; index < TEST_IMAGE_CAPTION.length; index++) {
            mMockResolver.insert(OnYardContract.ImageCaption.CONTENT_URI,
                    TEST_IMAGE_CAPTION[index].getContentValues());
        }

        for (int index = 0; index < TEST_IMAGE_RESHOOT.length; index++) {
            mMockResolver.insert(OnYardContract.ImageReshoot.CONTENT_URI,
                    TEST_IMAGE_RESHOOT[index].getContentValues());
        }

        for (Map.Entry<String, String> entry : TEST_CONFIG.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(OnYardContract.Config.COLUMN_NAME_KEY, entry.getKey());
            values.put(OnYardContract.Config.COLUMN_NAME_VALUE, entry.getValue());
            mMockResolver.insert(OnYardContract.Config.CONTENT_URI, values);
        }
        
        for (int index = 0; index < TEST_BRANCH.length; index++) 
        {
            branchValues[index] = TEST_BRANCH[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Branch.CONTENT_URI, branchValues);

        for (int index = 0; index < TEST_LOSS_TYPE.length; index++) 
        {
            lossTypeValues[index] = TEST_LOSS_TYPE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.LossType.CONTENT_URI, lossTypeValues);
        
        for (int index = 0; index < TEST_BODY_STYLE_SPECIALTY.length; index++) 
        {
            bodyStyleSpecialtyValues[index] = TEST_BODY_STYLE_SPECIALTY[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.BodyStyleSpecialty.CONTENT_URI, bodyStyleSpecialtyValues);
        
        for (int index = 0; index < TEST_LICENSE_PLATE_CONDITION.length; index++) 
        {
            licensePlateConditionValues[index] = TEST_LICENSE_PLATE_CONDITION[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.LicensePlateCondition.CONTENT_URI, licensePlateConditionValues);
        
        for (int index = 0; index < TEST_ODOMETER_READING_TYPE.length; index++) 
        {
            odometerReadingTypeValues[index] = TEST_ODOMETER_READING_TYPE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.OdometerReadingType.CONTENT_URI, odometerReadingTypeValues);
        
        for (int index = 0; index < TEST_PUBLIC_VIN.length; index++) 
        {
            publicVinValues[index] = TEST_PUBLIC_VIN[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.PublicVIN.CONTENT_URI, publicVinValues);
        
        for (int index = 0; index < TEST_SALVAGE_CONDITION.length; index++) 
        {
            salvageConditionValues[index] = TEST_SALVAGE_CONDITION[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SalvageCondition.CONTENT_URI, salvageConditionValues);
        
        for (int index = 0; index < TEST_SALVAGE_TYPE.length; index++) 
        {
            salvageTypeValues[index] = TEST_SALVAGE_TYPE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SalvageType.CONTENT_URI, salvageTypeValues);
        
        for (int index = 0; index < TEST_STATE.length; index++) 
        {
            stateValues[index] = TEST_STATE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.State.CONTENT_URI, stateValues);

        for (int index = 0; index < TEST_CHECKIN_FIELD.length; index++) {
            checkinFieldValues[index] = TEST_CHECKIN_FIELD[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.CheckinField.CONTENT_URI, checkinFieldValues);

        for (int index = 0; index < TEST_CHECKIN_TEMPLATE.length; index++) {
            checkinTemplateValues[index] = TEST_CHECKIN_TEMPLATE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.CheckinTemplate.CONTENT_URI, checkinTemplateValues);
        
        for (int index = 0; index < TEST_SLA_SALVAGE_ENHANCEMENT.length; index++) 
        {
        	slaSalvageEnhancementValues[index] = TEST_SLA_SALVAGE_ENHANCEMENT[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SlaSalvageEnhancement.CONTENT_URI, slaSalvageEnhancementValues);
        
        for (int index = 0; index < TEST_SALVAGE_ENHANCEMENT.length; index++) 
        {
        	salvageEnhancementValues[index] = TEST_SALVAGE_ENHANCEMENT[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SalvageEnhancement.CONTENT_URI, salvageEnhancementValues);

        for (int index = 0; index < TEST_ENHANCEMENT.length; index++) 
        {
        	enhancementValues[index] = TEST_ENHANCEMENT[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.Enhancement.CONTENT_URI, enhancementValues);

        for (int index = 0; index < TEST_DISABLED_ENHANCEMENT.length; index++) 
        {
        	disabledEnhancementValues[index] = TEST_DISABLED_ENHANCEMENT[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.DisabledEnhancement.CONTENT_URI, disabledEnhancementValues);

        for (int index = 0; index < TEST_SYNC_WINDOW.length; index++) 
        {
        	syncWindowValues[index] = TEST_SYNC_WINDOW[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SyncWindow.CONTENT_URI, syncWindowValues);

        for (int index = 0; index < TEST_SYNC_WINDOW_EXCEPTION.length; index++) 
        {
        	syncWindowExceptionValues[index] = TEST_SYNC_WINDOW_EXCEPTION[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYardContract.SyncWindowException.CONTENT_URI, syncWindowExceptionValues);
    }
    
    /*
     * Tests the provider's publicly available URIs. If the URI is not one that the provider
     * understands, the provider should throw an exception. It also tests the provider's getType()
     * method for each URI, which should return the MIME type associated with the URI.
     */
    public void testUriAndGetType() {
        String mimeType = mMockResolver.getType(OnYardContract.Vehicles.CONTENT_URI);
        assertEquals(OnYardContract.Vehicles.CONTENT_TYPE, mimeType);

        Uri vehicleIdUri = ContentUris.withAppendedId(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, 1);

        mimeType = mMockResolver.getType(vehicleIdUri);
        assertEquals(OnYardContract.Vehicles.CONTENT_ITEM_TYPE, mimeType);

        mimeType = mMockResolver.getType(INVALID_URI);
    }
    
    /*
     * Tests the provider's public API for querying data in the table, using the URI for
     * a dataset of records.
     */
    public void testQueriesOnVehiclesUri() {
        // Defines a projection of column names to return for a query - everything except lat and long
        final String[] TEST_PROJECTION = { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_VIN,
                OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_AISLE,
                OnYardContract.Vehicles.COLUMN_NAME_STALL,
                OnYardContract.Vehicles.COLUMN_NAME_COLOR,
                OnYardContract.Vehicles.COLUMN_NAME_YEAR, 
                OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                OnYardContract.Vehicles.COLUMN_NAME_MODEL,
                OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                OnYardContract.Vehicles.COLUMN_NAME_STATUS,
                OnYardContract.Vehicles.COLUMN_NAME_DAMAGE,
                OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_HAS_IMAGES,
                OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_RUN_DRIVE_IND,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX,
                OnYardContract.Vehicles.COLUMN_NAME_ADMIN_BRANCH,
                OnYardContract.Vehicles.COLUMN_NAME_LOSS_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER
        };
        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String STOCK_NUMBER_SELECTION = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER
                + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS =
        	STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION;

         // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Stock8", "Stock5", "Stock0" };

         // Defines a query sort order
        final String SORT_ORDER = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,  // the URI for the
                                                                                 // main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

         // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

         // Query subtest 2.
         // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,  // the URI for the main
                                                                          // data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_VEHICLES.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,  // the
                                                                                           // URI
                                                                                           // for
                                                                                           // the
                                                                                           // main
                                                                                           // data
                                                                                           // table
              TEST_PROJECTION,            // get all columns except id, lat, and long
              null,                       // no selection columns, get all the records
              null,                       // no selection criteria
              null                        // use default sort order
        );

        // Asserts that the number of columns in the cursor is the same as in the projection + salvage provider
        // name and sale doc type desc

        assertEquals(TEST_PROJECTION.length + 3, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));
        assertEquals(TEST_PROJECTION[3], projectionCursor.getColumnName(3));
        assertEquals(TEST_PROJECTION[4], projectionCursor.getColumnName(4));
        assertEquals(TEST_PROJECTION[5], projectionCursor.getColumnName(5));
        assertEquals(TEST_PROJECTION[6], projectionCursor.getColumnName(6));
        assertEquals(TEST_PROJECTION[7], projectionCursor.getColumnName(7));
        assertEquals(TEST_PROJECTION[8], projectionCursor.getColumnName(8));
        assertEquals(TEST_PROJECTION[9], projectionCursor.getColumnName(9));
        assertEquals(TEST_PROJECTION[10], projectionCursor.getColumnName(10));
        assertEquals(TEST_PROJECTION[11], projectionCursor.getColumnName(11));
        assertEquals(TEST_PROJECTION[12], projectionCursor.getColumnName(12));
        assertEquals(TEST_PROJECTION[13], projectionCursor.getColumnName(13));
        assertEquals(TEST_PROJECTION[14], projectionCursor.getColumnName(14));
        assertEquals(TEST_PROJECTION[15], projectionCursor.getColumnName(15));
        assertEquals(TEST_PROJECTION[16], projectionCursor.getColumnName(16));
        assertEquals(TEST_PROJECTION[17], projectionCursor.getColumnName(17));
        assertEquals(TEST_PROJECTION[18], projectionCursor.getColumnName(18));
        assertEquals(TEST_PROJECTION[19], projectionCursor.getColumnName(19));
        assertEquals(TEST_PROJECTION[20], projectionCursor.getColumnName(20));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, // the URI for
                                                                                    // the main data
                                                                                    // table
            TEST_PROJECTION,           // get all columns except id, lat, and long
            SELECTION_COLUMNS,         // select on the stock number column
            SELECTION_ARGS,            // select stock numbers "Stock8", "Stock5", or "Stock0"
            SORT_ORDER                 // sort descending on the stock number column
        );

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {

            // Asserts that the selection argument at the current index matches the value of
            // the stock number column (column 0) in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(0));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }
    
    public void testQueriesOnDataPendingSyncUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = { OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID,
                OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID,
                OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME,
                OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_TEXT,
                OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_INT
        };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String selection = OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME
                + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS =
                selection + " OR " + selection + " OR " + selection;

         // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "JsonName1", "JsonName3", "JsonName5" };

         // Defines a query sort order
        final String SORT_ORDER = OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + " ASC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null, null,
                null,
                null);

         // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

         // Query subtest 2.
         // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null, null, null,
                null
        );

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_DATA_PENDING_SYNC.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI,
                TEST_PROJECTION, null, null, null);

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));
        assertEquals(TEST_PROJECTION[3], projectionCursor.getColumnName(3));
        assertEquals(TEST_PROJECTION[4], projectionCursor.getColumnName(4));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI,
                TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value of
            // the json name column (column 2) in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }
    
    public void testQueriesOnImageReshootUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = { OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER,
                OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET};

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String selection = OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS = selection + " OR " + selection + " OR " + selection;

        // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Stock1", "Stock03", "Stock4" };

        // Defines a query sort order
        final String SORT_ORDER = OnYardContract.ImageReshoot.COLUMN_NAME_ID + " ASC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI, null, null,
                null, null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI, null, null, null,
                null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_IMAGE_RESHOOT.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI,
                TEST_PROJECTION, null, null, null);

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI,
                TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value of
            // the stock number column (column 0) in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }

    public void testQueriesOnVehiclesReshootUri() {
        // Defines the arguments for the selection columns.
        final String[] RESHOOT_STOCK_NUMBERS = { "Stock4", "Stock1" };

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.Vehicles.RESHOOT_URI, null, null, null,
                null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.Vehicles.RESHOOT_URI, null, null, null, null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(RESHOOT_STOCK_NUMBERS.length, cursor.getCount());

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.Vehicles.RESHOOT_URI,
                null, null, null, null);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(RESHOOT_STOCK_NUMBERS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value of
            // the stock number column (column 0) in the current record of the cursor
            assertEquals(RESHOOT_STOCK_NUMBERS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(RESHOOT_STOCK_NUMBERS.length, index);
    }

    public void testQueriesOnConfigUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = { OnYardContract.Config.COLUMN_NAME_KEY,
                OnYardContract.Config.COLUMN_NAME_VALUE };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String selection = OnYardContract.Config.COLUMN_NAME_VALUE + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS = selection + " OR " + selection + " OR " + selection;

        // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Value1", "Value3", "Value5" };

        // Defines a query sort order
        final String SORT_ORDER = OnYardContract.Config.COLUMN_NAME_KEY + " ASC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, null, null, null,
                null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, null, null, null, null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_CONFIG.size(), cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI,
                TEST_PROJECTION, null, null, null);

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, TEST_PROJECTION,
                SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value in the
            // current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.Config.COLUMN_NAME_VALUE)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }

    public void testQueriesOnImageCaptionsUri() {
     // Defines a projection of column names to return for a query
     final String[] TEST_PROJECTION = { OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER,
     OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE,
                OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID,
     OnYardContract.ImageCaption.COLUMN_NAME_CAPTION,
                OnYardContract.ImageCaption.COLUMN_NAME_DEFAULT_FOCUS_MODE,
                OnYardContract.ImageCaption.COLUMN_NAME_MIN_IMAGE_HEIGHT,
                OnYardContract.ImageCaption.COLUMN_NAME_MIN_IMAGE_WIDTH,
                OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_SEQUENCE };
    
     // Defines a selection column for the query. When the selection columns are passed
     // to the query, the selection arguments replace the placeholders.
        final String selection = OnYardContract.ImageCaption.COLUMN_NAME_CAPTION + " = " + "? AND "
                + OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=?";
    
     // Defines the selection columns for a query.
     final String SELECTION_COLUMNS = selection + " OR " + selection + " OR " + selection;
    
     // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Caption1", "1", "Caption2", "1", "Caption3", "1" };
    
     // Defines a query sort order
     final String SORT_ORDER = OnYardContract.ImageCaption.COLUMN_NAME_CAPTION + " ASC";
    
     // Query subtest 1.
     // If there are no records in the table, the returned cursor from a query should be empty.
     Cursor cursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI, null, null,
     null, null);
    
     // Asserts that the returned cursor contains no records
     assertEquals(0, cursor.getCount());
    
     // Query subtest 2.
     // If the table contains records, the returned cursor from a query should contain records.
    
     // Inserts the test data into the provider's underlying data source
     insertData();
    
     // Gets all the columns for all the rows in the table
     cursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI, null, null, null,
     null);
    
     // Asserts that the returned cursor contains the same number of rows as the size of the
     // test data array.
     assertEquals(TEST_IMAGE_CAPTION.length, cursor.getCount());
    
     // Query subtest 3.
     // A query that uses a projection should return a cursor with the same number of columns
     // as the projection, with the same names, in the same order.
     Cursor projectionCursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI,
     TEST_PROJECTION, null, null, null);
    
     // Asserts that the number of columns in the cursor is the same as in the projection
     assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());
    
     // Asserts that the names of the columns in the cursor and in the projection are the same.
     // This also verifies that the names are in the same order.
     assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
     assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
     assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));
     assertEquals(TEST_PROJECTION[3], projectionCursor.getColumnName(3));
     assertEquals(TEST_PROJECTION[4], projectionCursor.getColumnName(4));
        assertEquals(TEST_PROJECTION[5], projectionCursor.getColumnName(5));
        assertEquals(TEST_PROJECTION[6], projectionCursor.getColumnName(6));
        assertEquals(TEST_PROJECTION[7], projectionCursor.getColumnName(7));
    
     // Query subtest 4
     // A query that uses selection criteria should return only those rows that match the
     // criteria. Use a projection so that it's easy to get the data in a particular column.
     projectionCursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI,
     TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);
    
     // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length / 2, projectionCursor.getCount());
    
     int index = 0;
    
     while (projectionCursor.moveToNext()) {
     // Asserts that the selection argument at the current index matches the value in the
     // current record of the cursor
     assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_CAPTION)));

            index++;

            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE)));
    
     index++;
     }
    
     // Asserts that the index pointer is now the same as the number of selection arguments, so
     // that the number of arguments tested is exactly the same as the number of rows returned.
     assertEquals(SELECTION_ARGS.length, index);
    }

    public void testQueriesOnImageTypesUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = { OnYardContract.ImageType.COLUMN_NAME_ID,
                OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME };
       
        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String selection = OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + " = " + "?";
       
        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS = selection + " OR " + selection + " OR " + selection;
       
        // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "ImageType1", "ImageType2", "ImageType3" };
       
        // Defines a query sort order
        final String SORT_ORDER = OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + " ASC";
       
        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI, null, null,
        null, null);
       
        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());
       
        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.
       
        // Inserts the test data into the provider's underlying data source
        insertData();
       
        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI, null, null, null,
        null);
       
        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_IMAGE_TYPE.length, cursor.getCount());
       
        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI,
        TEST_PROJECTION, null, null, null);
       
        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());
       
        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
       
        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI,
        TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);
       
        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());
       
        int index = 0;
       
        while (projectionCursor.moveToNext()) {
        // Asserts that the selection argument at the current index matches the value in the
        // current record of the cursor
        assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
        .getColumnIndex(OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME)));
       
        index++;
        }
       
        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
       }
    
    public void testQueriesOnDataPendingSyncIDUri() {
        // Creates a projection includes the blob id column
        final String[] DATA_ID_PROJECTION = { OnYardContract.DataPendingSync.COLUMN_NAME_ID,
                OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME };

        // Query subtest 1.
        // Tests that a query against an empty table returns null.
        Uri blobIdUri = ContentUris.withAppendedId(
                OnYardContract.DataPendingSync.CONTENT_ID_URI_BASE, 1);

        // Queries the table with the id URI. This should return an empty cursor.
        Cursor cursor = mMockResolver.query(blobIdUri, null, null, null, null);

        // Asserts that the cursor is null.
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // Tests that a query against a table containing records returns a single record whose id
        // is the one requested in the URI provided.

        // Inserts the test data into the provider's underlying data source.
        insertData();

        // Queries the table using the URI for the full table.
        cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null, null, null,
                null);

        // Moves to the cursor's first row, and asserts that this did not fail.
        assertTrue(cursor.moveToFirst());

        // Saves the first record's id.
        long inputDataId = cursor.getLong(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_ID));

        // Builds a URI based on the blob's id URI base and the saved blob id.
        blobIdUri = ContentUris.withAppendedId(OnYardContract.DataPendingSync.CONTENT_ID_URI_BASE,
                inputDataId);

        // Queries the table using the id URI, which returns a single record with the
        // specified blob id, matching the selection criteria provided.
        cursor = mMockResolver.query(blobIdUri, DATA_ID_PROJECTION, null, null, null);

        // Asserts that the cursor contains only one row.
        assertEquals(1, cursor.getCount());

        // Moves to the cursor's first row, and asserts that this did not fail.
        assertTrue(cursor.moveToFirst());

        // Asserts that the vehicle stock number passed to the provider is the same as the vehicle
        // stock number returned.
        assertEquals(inputDataId,
                cursor.getInt(cursor.getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_ID)));
    }

    /*
     * Tests the provider's public API for querying data in the table, using the URI for
     * a dataset of records.
     */
    public void testJoinQueriesOnVehiclesUri() {
        // Defines a projection of column names to return for a query - stock number and join columns
        final String[] TEST_PROJECTION = { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_DAMAGE,
                OnYardContract.Vehicles.COLUMN_NAME_COLOR,
                OnYardContract.Vehicles.COLUMN_NAME_STATUS,
                OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER
        };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String STOCK_NUMBER_SELECTION = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER
                + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS =
        	STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION;

         // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Stock4", "Stock2", "Stock0" };
        
        // Defines the arguments for the corresponding joined description columns
        final String[] STATUS_ARGS = { "St4", "St2", "St0" };
        final String[] COLOR_ARGS = { "ColDesc4", "ColDesc2", "ColDesc0" };
        final String[] DAMAGE_ARGS = { "DDesc4", "DDesc2", "DDesc0" };
        final String[] SALE_DOC_TYPE_ARGS = { TEST_SALE_DOC_TYPE[4].getSaleDocTypeDescription(),
                TEST_SALE_DOC_TYPE[2].getSaleDocTypeDescription(),
                TEST_SALE_DOC_TYPE[0].getSaleDocTypeDescription() };
        final String[] SALVAGE_PROVIDER_ARGS = { TEST_SALVAGE_PROVIDER[4].getSalvageProviderName(),
                TEST_SALVAGE_PROVIDER[2].getSalvageProviderName(),
                TEST_SALVAGE_PROVIDER[0].getSalvageProviderName() };

         // Defines a query sort order
        final String SORT_ORDER = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,  // the URI for the
                                                                                 // main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

         // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

         // Query subtest 2.
         // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,  // the URI for the main
                                                                          // data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_VEHICLES.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI,
              TEST_PROJECTION,            // get stock number and join columns
              null,                       // no selection columns, get all the records
              null,                       // no selection criteria
              null                        // use default sort order
        );

        // Asserts that the number of columns in the cursor is the same as in the projection plus
        // sale doc type description
        assertEquals(TEST_PROJECTION.length + 3, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));
        assertEquals(TEST_PROJECTION[3], projectionCursor.getColumnName(3));
        assertEquals(TEST_PROJECTION[4], projectionCursor.getColumnName(4));
        assertEquals(TEST_PROJECTION[5], projectionCursor.getColumnName(5));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, // the URI for
                                                                                    // the main data
                                                                                    // table
            TEST_PROJECTION,           // get stock number and join columns
            SELECTION_COLUMNS,         // select on the stock number column
            SELECTION_ARGS,            // select stock numbers "Stock4", "Stock2", or "Stock0"
            SORT_ORDER                 // sort descending on the stock number column
        );

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) 
        {
            // Asserts that the selection argument at the current index matches the value of
            // the columns in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(0));
            assertEquals(DAMAGE_ARGS[index], projectionCursor.getString(1));
            assertEquals(COLOR_ARGS[index], projectionCursor.getString(2));
            assertEquals(STATUS_ARGS[index], projectionCursor.getString(3));
            assertEquals(SALE_DOC_TYPE_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.SaleDocType.COLUMN_NAME_DESCRIPTION)));
            assertEquals(SALVAGE_PROVIDER_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.SalvageProvider.COLUMN_NAME_DESCRIPTION)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }
    
    /*
     * Tests queries against the provider, using the vehicle id URI. This URI encodes a single
     * record ID. The provider should only return 0 or 1 record.
     */
    public void testQueriesOnVehicleIdUri() {
      // Defines the selection column for a query. The "?" is replaced by entries in the
      // selection argument array
        final String SELECTION_COLUMNS = OnYardContract.Vehicles.COLUMN_NAME_VIN + " = "
                + "?";

      // Defines the argument for the selection column.
      
        final String[] SELECTION_ARGS = { "VIN1" };

      // A sort order for the query.
        final String SORT_ORDER = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " ASC";

        // Creates a projection includes the vehicle stock number column, so that vehicle stock
        // number can be retrieved.
        final String[] VEHICLE_ID_PROJECTION = { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_VIN };  // The vehicle's VIN

      // Query subtest 1.
      // Tests that a query against an empty table returns null.

        Uri vehicleStockNumUri = Uri.withAppendedPath(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock1");

        // Queries the table with the vehicles stock number URI. This should return an empty cursor.
        Cursor cursor = mMockResolver.query(vehicleStockNumUri, null, null, null, null
      );

      // Asserts that the cursor is null.
      assertEquals(0,cursor.getCount());

      // Query subtest 2.
        // Tests that a query against a table containing records returns a single record whose stock
        // number
      // is the one requested in the URI provided.

      // Inserts the test data into the provider's underlying data source.
      insertData();

      // Queries the table using the URI for the full table.
        cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, VEHICLE_ID_PROJECTION,
                SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor.moveToFirst());

        // Saves the record's stock number.
        String inputVehicleStockNum = cursor.getString(cursor
                .getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER));

        // Builds a URI based on the provider's stock number URI base and the saved vehicle stock
        // number.
        vehicleStockNumUri = Uri.withAppendedPath(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE,
                inputVehicleStockNum);

        // Queries the table using the stock number URI, which returns a single record with the
        // specified vehicle stock number, matching the selection criteria provided.
        cursor = mMockResolver.query(vehicleStockNumUri, // the URI for a single vehicle
                VEHICLE_ID_PROJECTION, null, null, SORT_ORDER
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor.moveToFirst());

        // Asserts that the vehicle stock number passed to the provider is the same as the vehicle
        // stock number returned.
        assertEquals(inputVehicleStockNum, cursor.getString(cursor
                .getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER)));
    }
    
    /*
     * Tests queries against the provider, using the vehicle id URI. This URI encodes a single
     * record ID. The provider should only return 0 or 1 record.
     */
    public void testQueriesOnVehicleStockNumUri() {
      // Defines the selection column for a query. The "?" is replaced by entries in the
      // selection argument array
        final String SELECTION_COLUMNS = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = "
                + "?";

      // Defines the argument for the selection column.
      final String[] SELECTION_ARGS = { "Stock6" };

      // A sort order for the query.
        final String SORT_ORDER = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " ASC";

      // Creates a projection including the vehicle id column, so that vehicle stock number can be retrieved.
        final String[] VEHICLE_ID_PROJECTION = { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_VIN,
                OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_AISLE,
                OnYardContract.Vehicles.COLUMN_NAME_STALL,
                OnYardContract.Vehicles.COLUMN_NAME_COLOR,
                OnYardContract.Vehicles.COLUMN_NAME_YEAR, OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                OnYardContract.Vehicles.COLUMN_NAME_MODEL,
                OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                OnYardContract.Vehicles.COLUMN_NAME_STATUS,
                OnYardContract.Vehicles.COLUMN_NAME_DAMAGE,
                OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_HAS_IMAGES,
                OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE,
                OnYardContract.Vehicles.COLUMN_NAME_RUN_DRIVE_IND,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER,
      };

      // Query subtest 1.
      // Tests that a query against an empty table returns null.

      // Constructs a URI that matches the provider's vehicles id URI pattern, using an arbitrary
      // value of Stock1 as the vehicle stock number.
        Uri vehicleIdUri = Uri.withAppendedPath(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock6");

      // Queries the table with the vehicles ID URI. This should return an empty cursor.
      Cursor cursor1 = mMockResolver.query(
    	  vehicleIdUri, // URI pointing to a single record
          null,      // no projection, get all the columns for each record
          null,      // no selection criteria, get all the records in the table
          null,      // no need for selection arguments
          null       // default sort, by ascending stock number
      );

      // Asserts that the cursor is null.
      assertEquals(0,cursor1.getCount());

      // Query subtest 2.
      // Tests that a query against a table containing records returns a single record whose ID
      // is the one requested in the URI provided.

      // Inserts the test data into the provider's underlying data source.
      insertData();

      // Queries the table using the URI for the full table.
        cursor1 = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, // the base URI for the
                                                                           // table
          VEHICLE_ID_PROJECTION,        // returns the ID and stock number columns of rows
          SELECTION_COLUMNS,         // select based on the stock number column
          SELECTION_ARGS,            // select stock of "Stock1"
          SORT_ORDER                 // sort order returned is by stock number, ascending
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor1.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor1.moveToFirst());

      // Saves the record's vehicle ID.
      String inputVehicleStock = cursor1.getString(0);

      // Queries the table using the content ID URI, which returns a single record with the
      // specified vehicle ID, matching the selection criteria provided.
      Cursor cursor2 = mMockResolver.query(vehicleIdUri, // the URI for a single vehicle
          VEHICLE_ID_PROJECTION,                 // same projection, get ID and stock number columns
          null,                  // same selection, based on stock number column
          null,                     // same selection arguments, stock number = "Stock1"
          SORT_ORDER                          // same sort order returned, by stock number, ascending
      );
      
      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor2.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor2.moveToFirst());

      // Asserts that the vehicle ID passed to the provider is the same as the vehicle ID returned.
      assertEquals(inputVehicleStock, cursor2.getString(0));
      
      assertEquals(cursor1.getString(0), cursor2.getString(0));
      assertEquals(cursor1.getString(1), cursor2.getString(1));
      assertEquals(cursor1.getString(2), cursor2.getString(2));
      assertEquals(cursor1.getString(3), cursor2.getString(3));
      assertEquals(cursor1.getInt(4), cursor2.getInt(4));
      assertEquals(cursor1.getString(5), cursor2.getString(5));
      assertEquals(cursor1.getInt(6), cursor2.getInt(6));
      assertEquals(cursor1.getString(7), cursor2.getString(7));
      assertEquals(cursor1.getString(8), cursor2.getString(8));
      assertEquals(cursor1.getString(9), cursor2.getString(9));
      assertEquals(cursor1.getString(10), cursor2.getString(10));
      assertEquals(cursor1.getString(11), cursor2.getString(11));
        assertEquals(cursor1.getInt(12), cursor2.getInt(12));
        assertEquals(cursor1.getInt(13), cursor2.getInt(13));
        assertEquals(cursor1.getInt(14), cursor2.getInt(14));
        assertEquals(cursor1.getInt(15), cursor2.getInt(15));
        assertEquals(cursor1.getInt(16), cursor2.getInt(16));
        assertEquals(cursor1.getInt(17), cursor2.getInt(17));
        assertEquals(cursor1.getInt(18), cursor2.getInt(18));
    }

    public void testQueriesOnCheckinFieldUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = { OnYardContract.CheckinField.COLUMN_NAME_ID,
                OnYardContract.CheckinField.COLUMN_NAME_FIELD_TYPE_DESCRIPTION,
                OnYardContract.CheckinField.COLUMN_NAME_INPUT_TYPE_DESCRIPTION,
                OnYardContract.CheckinField.COLUMN_NAME_CAPTION,
                OnYardContract.CheckinField.COLUMN_NAME_FEATURE_CODE,
                OnYardContract.CheckinField.COLUMN_NAME_FEATURE_GROUP_NUMBER,
                OnYardContract.CheckinField.COLUMN_NAME_IS_REQUIRED,
                OnYardContract.CheckinField.COLUMN_NAME_MIN_INT_VALUE,
                OnYardContract.CheckinField.COLUMN_NAME_MAX_INT_VALUE,
                OnYardContract.CheckinField.COLUMN_NAME_MAX_STRING_LENGTH,
                OnYardContract.CheckinField.COLUMN_NAME_DATA_MEMBER_NAME };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String FIELD_ID_SELECTION = OnYardContract.CheckinField.COLUMN_NAME_ID + " = ?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS = FIELD_ID_SELECTION + " OR " + FIELD_ID_SELECTION + " OR "
                + FIELD_ID_SELECTION;

        // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "4", "3", "0" };

        // Defines a query sort order
        final String SORT_ORDER = OnYardContract.CheckinField.COLUMN_NAME_ID + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI, null, null,
                null, null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI, null, null, null,
                null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_CHECKIN_FIELD.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI,
                TEST_PROJECTION, null, null, null);

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));
        assertEquals(TEST_PROJECTION[3], projectionCursor.getColumnName(3));
        assertEquals(TEST_PROJECTION[4], projectionCursor.getColumnName(4));
        assertEquals(TEST_PROJECTION[5], projectionCursor.getColumnName(5));
        assertEquals(TEST_PROJECTION[6], projectionCursor.getColumnName(6));
        assertEquals(TEST_PROJECTION[7], projectionCursor.getColumnName(7));
        assertEquals(TEST_PROJECTION[8], projectionCursor.getColumnName(8));
        assertEquals(TEST_PROJECTION[9], projectionCursor.getColumnName(9));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI,
                TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value of
            // the sorting column in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_ID)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }

    public void testQueriesOnCheckinTemplateUri() {
        // Defines a projection of column names to return for a query
        final String[] TEST_PROJECTION = {
                OnYardContract.CheckinTemplate.COLUMN_NAME_CHECKIN_FIELD_ID,
                OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE,
                OnYardContract.CheckinTemplate.COLUMN_NAME_SEQUENCE };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String FIELD_ID_SELECTION = OnYardContract.CheckinTemplate.COLUMN_NAME_SEQUENCE
                + " = ?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS = FIELD_ID_SELECTION + " OR " + FIELD_ID_SELECTION + " OR "
                + FIELD_ID_SELECTION;

        // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "4", "1", "0" };

        // Defines a query sort order
        final String SORT_ORDER = OnYardContract.CheckinTemplate.COLUMN_NAME_SEQUENCE + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI, null, null,
                null, null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        // Query subtest 2.
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI, null, null, null,
                null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_CHECKIN_FIELD.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI,
                TEST_PROJECTION, null, null, null);

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI,
                TEST_PROJECTION, SELECTION_COLUMNS, SELECTION_ARGS, SORT_ORDER);

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {
            // Asserts that the selection argument at the current index matches the value of
            // the sorting column in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(projectionCursor
                    .getColumnIndex(OnYardContract.CheckinTemplate.COLUMN_NAME_SEQUENCE)));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }

    /*
     *  Tests inserts into the data model.
     */
    public void testVehicleInserts() {
        // Creates a new vehicle instance with ID of 30.
        VehicleInfo vehicle = new VehicleInfo("Stock30", "VIN30", "Claim30", 30, 30, "Z", 30,
                "Col30", 30, "Make30", "Model30", 30, "St30", "D30", 3, true, 3, true, 300, 111, false, "L30", 30, 333, 1412928000,151200000);

        // Insert subtest 1.
        // Inserts a row using the new vehicle instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        Uri rowUri = mMockResolver.insert(OnYardContract.Vehicles.CONTENT_URI,  // the main table URI
            vehicle.getContentValues()     // the map of values to insert as a new record
        );

        List<String> uriSegments = rowUri.getPathSegments();
        // Parses the returned URI to get the vehicle ID of the new vehicle. The ID is used in subtest 2.
        String vehicleStockNum = uriSegments.get(uriSegments.size() - 1);

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, // the main table
                                                                                 // URI
            null,                      // no projection, return all the columns
            null,                      // no selection criteria, return all the rows in the model
            null,                      // no selection arguments
            null                       // default sort order
        );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int stockIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER);
        int VINIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_VIN);
        int claimIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_CLAIM_NUMBER);
        int latIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_LATITUDE);
        int longIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_LONGITUDE);
        int aisleIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_AISLE);
        int stallIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STALL);
        int colorIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_COLOR);
        int yearIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_YEAR);
        int makeIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_MAKE);
        int modelIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_MODEL);
        int provIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER);
        int statusIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STATUS);
        int dmgIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_DAMAGE);
        int slvTypeIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_TYPE);
        int hasImgIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_HAS_IMAGES);
        int saleDocIndex = cursor.getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE);
        int runAndDriveIndex = cursor
                .getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_RUN_DRIVE_IND);
        int auctionDateIndex = cursor
                .getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the vehicleInfo object to the data at the column index in the cursor.
        assertEquals(vehicle.getStockNumber(), cursor.getString(stockIndex));
        assertEquals(vehicle.getVIN(), cursor.getString(VINIndex));
        assertEquals(vehicle.getClaimNumber(), cursor.getString(claimIndex));
        assertEquals(vehicle.getLatitude(), cursor.getFloat(latIndex));
        assertEquals(vehicle.getLongitude(), cursor.getFloat(longIndex));
        assertEquals(vehicle.getAisle(), cursor.getString(aisleIndex));
        assertEquals(vehicle.getStall(), cursor.getInt(stallIndex));
        assertEquals(vehicle.getColorDescription(), cursor.getString(colorIndex));
        assertEquals(vehicle.getYear(), cursor.getInt(yearIndex));
        assertEquals(vehicle.getMake(), cursor.getString(makeIndex));
        assertEquals(vehicle.getModel(), cursor.getString(modelIndex));
        assertEquals(vehicle.getSalvageProviderId(), cursor.getInt(provIndex));
        assertEquals(vehicle.getStatusCode(), cursor.getString(statusIndex));
        assertEquals(vehicle.getDamage(), cursor.getString(dmgIndex));
        assertEquals(vehicle.getSalvageType(), cursor.getInt(slvTypeIndex));
        assertEquals(vehicle.hasImages() ? ImageSet.ENHANCEMENT : ImageSet.CHECK_IN,
                cursor.getInt(hasImgIndex));
        assertEquals(vehicle.getSaleDocTypeId(), cursor.getInt(saleDocIndex));
        assertEquals(vehicle.isRunAndDrive() ? 1 : 0, cursor.getInt(runAndDriveIndex));
        assertEquals(vehicle.getAuctionDate(), cursor.getInt(auctionDateIndex));

        // Insert subtest 2.
        // Tests that we can't insert a record whose id value already exists.

        // Defines a ContentValues object so that the test can add a vehicle ID to it.
        ContentValues values = vehicle.getContentValues();

        // Adds the vehicle ID retrieved in subtest 1 to the ContentValues object.
        values.put(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER, vehicleStockNum);

        // Tries to insert this record into the table. This should fail and drop into the
        // catch block. If it succeeds, issue a failure message.
        try {
            rowUri = mMockResolver.insert(OnYardContract.Vehicles.CONTENT_URI, values);
            fail("Expected insert failure for existing record but insert succeeded.");
        } catch (Exception e) {
            // succeeded, so do nothing.
        }
    }

    public void testConfigInserts() {
        String testKey = "TestKey";
        String testValue = "TestValue";

        ContentValues testContentValues = new ContentValues();
        testContentValues.put(OnYardContract.Config.COLUMN_NAME_KEY, testKey);
        testContentValues.put(OnYardContract.Config.COLUMN_NAME_VALUE, testValue);

        // Insert subtest 1.
        // Inserts test row.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.Config.CONTENT_URI, testContentValues);

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, null, null, null,
                null);

        // Asserts that there is only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the vehicleInfo object to the data at the column index in the cursor.
        assertEquals(testKey,
                cursor.getString(cursor.getColumnIndex(OnYardContract.Config.COLUMN_NAME_KEY)));
        assertEquals(testValue,
                cursor.getString(cursor.getColumnIndex(OnYardContract.Config.COLUMN_NAME_VALUE)));

        // Insert subtest 2.
        // Tests that we can't insert a record whose primary key value already exists.

        // Tries to insert the same values from subtest 1 into the table. This should fail and drop
        // into the catch block. If it succeeds, issue a failure message.
        try {
            mMockResolver.insert(OnYardContract.Config.CONTENT_URI, testContentValues);
            fail("Expected insert failure for existing record but insert succeeded.");
        }
        catch (Exception e) {
            // succeeded, so do nothing.
        }
    }

    /*
     * Tests inserts into the data model.
     */
    public void testImageReshootInserts() {
        // Creates a new reshoot instance
        ImageReshootInfo reshoot = new ImageReshootInfo("Stock30", 9, 1, false);

        // Insert subtest 1.
        // Inserts a row using the new reshoot instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.ImageReshoot.CONTENT_URI,  // the main table
                                                                                   // URI
                reshoot.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI, // the main
                                                                                     // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int stockIndex = cursor
                .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER);
        int imageOrderIndex = cursor
                .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER);
        int imageSetIndex = cursor
                .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the reshoot object to the data at the column index in the cursor.
        assertEquals(reshoot.getStockNumber(), cursor.getString(stockIndex));
        assertEquals(reshoot.getImageOrder(), cursor.getInt(imageOrderIndex));
        assertEquals(reshoot.getImageSet(), cursor.getInt(imageSetIndex));
    }

    public void testSaleDocTypeInserts() {
        // Creates a new reshoot instance
        SaleDocTypeInfo saleDoc = new SaleDocTypeInfo(111, "Document 111");

        // Insert subtest 1.
        // Inserts a row using the new reshoot instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SaleDocType.CONTENT_URI,  // the main table
                                                                     // URI
                saleDoc.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SaleDocType.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int idIndex = cursor.getColumnIndex(OnYardContract.SaleDocType.COLUMN_NAME_ID);
        int descIndex = cursor.getColumnIndex(OnYardContract.SaleDocType.COLUMN_NAME_DESCRIPTION);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the reshoot object to the data at the column index in the cursor.
        assertEquals(saleDoc.getSaleDocTypeId(), cursor.getInt(idIndex));
        assertEquals(saleDoc.getSaleDocTypeDescription(), cursor.getString(descIndex));
    }

    public void testSalvageProviderInserts() {
        // Creates a new reshoot instance
        SalvageProviderInfo salvProv = new SalvageProviderInfo(222, "Salvage Provider 222", false);

        // Insert subtest 1.
        // Inserts a row using the new reshoot instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SalvageProvider.CONTENT_URI,  // the main table
                // URI
                salvProv.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageProvider.CONTENT_URI, // the main
                // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int idIndex = cursor.getColumnIndex(OnYardContract.SalvageProvider.COLUMN_NAME_ID);
        int descIndex = cursor
                .getColumnIndex(OnYardContract.SalvageProvider.COLUMN_NAME_DESCRIPTION);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the reshoot object to the data at the column index in the cursor.
        assertEquals(salvProv.getSalvageProviderId(), cursor.getInt(idIndex));
        assertEquals(salvProv.getSalvageProviderName(), cursor.getString(descIndex));
    }

    public void testBranchInserts() {
        // Creates a new branch instance
        BranchInfo branch = new BranchInfo("789", "Branch 789", "S9","PT", false);

        // Insert subtest 1.
        // Inserts a row using the new branch instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.Branch.CONTENT_URI,  // the main table
                                                                     // URI
                branch.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.Branch.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int numIndex = cursor.getColumnIndex(OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER);
        int nameIndex = cursor.getColumnIndex(OnYardContract.Branch.COLUMN_NAME_BRANCH_NAME);
        int stateIndex = cursor.getColumnIndex(OnYardContract.Branch.COLUMN_NAME_BRANCH_STATE);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the branch object to the data at the column index in the cursor.
        assertEquals(branch.getBranchNumber(), cursor.getString(numIndex));
        assertEquals(branch.getBranchName(), cursor.getString(nameIndex));
        assertEquals(branch.getBranchState(), cursor.getString(stateIndex));
    }
    
    
    public void testLossTypeInserts() {
        // Creates a new loss type instance
        LossTypeInfo lossType = new LossTypeInfo("L78", "Loss type 7", false);

        // Insert subtest 1.
        // Inserts a row using the new loss type instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.LossType.CONTENT_URI,  // the main table
                                                                     // URI
                lossType.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.LossType.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int codeIndex = cursor.getColumnIndex(OnYardContract.LossType.COLUMN_NAME_CODE);
        int descIndex = cursor.getColumnIndex(OnYardContract.LossType.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the loss type object to the data at the column index in the cursor.
        assertEquals(lossType.getLossTypeCode(), cursor.getString(codeIndex));
        assertEquals(lossType.getLossTypeDescription(), cursor.getString(descIndex));
    }

    public void testImageCaptionInserts() {
        ImageCaptionInfo testData = new ImageCaptionInfo(15, 1, 50, "TestCaption", "TestFocusMode",
                640, 480, 11, "Overlay1", false, 75, true, "Overlay1");
    
     // Insert subtest 1.
     // Inserts test row.
     // No assertion will be done. The insert() method either works or throws an Exception
     mMockResolver.insert(OnYardContract.ImageCaption.CONTENT_URI, testData.getContentValues());
    
     // Does a full query on the table. Since insertData() hasn't yet been called, the
     // table should only contain the record just inserted.
     Cursor cursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI, null, null,
     null, null);
    
     // Asserts that there is only 1 record.
     assertEquals(1, cursor.getCount());
    
     // Moves to the first (and only) record in the cursor and asserts that this worked.
     assertTrue(cursor.moveToFirst());
    
     long testDataId = cursor.getLong(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_ID));
    
     // Tests each column in the returned cursor against the data that was inserted, comparing
     // the field in the vehicleInfo object to the data at the column index in the cursor.
     assertEquals(testData.getImageOrder(), cursor.getInt(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER)));
     assertEquals(testData.getSalvageType(), cursor.getInt(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE)));     
     assertEquals(testData.getImageTypeId(), cursor.getInt(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID)));
     assertEquals(testData.getCaption(), cursor.getString(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_CAPTION)));
     assertEquals(testData.getDefaultFocusMode(), cursor.getString(cursor
     .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_DEFAULT_FOCUS_MODE)));
        assertEquals(testData.getMinImageHeight(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_MIN_IMAGE_HEIGHT)));
        assertEquals(testData.getMinImageWidth(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_MIN_IMAGE_WIDTH)));
        assertEquals(testData.getImageSequence(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_SEQUENCE)));
        assertEquals(testData.getOverlayFileName(), cursor.getString(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_OVERLAY_FILE)));
        assertEquals(testData.getJpegQuality(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_JPEG_QUALITY)));
        assertEquals(testData.isLevelLineEnabled(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_LEVEL_LINE_ENABLED)) == 1);
    
     // Insert subtest 2.
     // Tests that we can't insert a record whose primary key value already exists.
    
     // Defines a ContentValues object so that the test can add an ID to it.
     ContentValues values = testData.getContentValues();
    
     // Adds the ID retrieved in subtest 1 to the ContentValues object.
     values.put(OnYardContract.ImageCaption.COLUMN_NAME_ID, testDataId);
    
     // Tries to insert this record into the table. This should fail and drop into the
     // catch block. If it succeeds, issue a failure message.
     try {
     mMockResolver.insert(OnYardContract.ImageCaption.CONTENT_URI, values);
     fail("Expected insert failure for existing record but insert succeeded.");
     }
     catch (Exception e) {
     // succeeded, so do nothing.
     }
    }

    public void testImageTypeInserts() {
        ImageTypeInfo testData = new ImageTypeInfo(15, "TestImageType", false);
       
        // Insert subtest 1.
        // Inserts test row.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.ImageType.CONTENT_URI, testData.getContentValues());
       
        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI, null, null,
        null, null);
       
        // Asserts that there is only 1 record.
        assertEquals(1, cursor.getCount());
       
        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());
       
        long testDataId = cursor.getLong(cursor
        .getColumnIndex(OnYardContract.ImageType.COLUMN_NAME_ID));
       
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the vehicleInfo object to the data at the column index in the cursor.
        assertEquals(testData.getImageTypeId(), cursor.getInt(cursor
        .getColumnIndex(OnYardContract.ImageType.COLUMN_NAME_ID)));
        assertEquals(testData.getImageTypeName(), cursor.getString(cursor
                .getColumnIndex(OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME)));

        // Insert subtest 2.
        // Tests that we can't insert a record whose primary key value already exists.
       
        // Defines a ContentValues object so that the test can add an ID to it.
        ContentValues values = testData.getContentValues();
       
        // Adds the ID retrieved in subtest 1 to the ContentValues object.
        values.put(OnYardContract.ImageType.COLUMN_NAME_ID, testDataId);
       
        // Tries to insert this record into the table. This should fail and drop into the
        // catch block. If it succeeds, issue a failure message.
        try {
        mMockResolver.insert(OnYardContract.ImageType.CONTENT_URI, values);
        fail("Expected insert failure for existing record but insert succeeded.");
        }
        catch (Exception e) {
        // succeeded, so do nothing.
        }
       }
    
    public void testDataPendingSyncInserts() {
        DataPendingSync testData = new DataPendingSync(1, "UUID", "JsonName", "ValueText", 3L, 3D);

        // Insert subtest 1.
        // Inserts test row.
        // No assertion will be done. The insert() method either works or throws an Exception
        Uri rowUri = mMockResolver.insert(OnYardContract.DataPendingSync.CONTENT_URI,
                testData.getContentValues());

        List<String> uriSegments = rowUri.getPathSegments();
        // Parses the returned URI to get the ID of the new row. The ID is used in subtest 2.
        String testDataId = uriSegments.get(uriSegments.size() - 1);

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null, null,
                null, null);

        // Asserts that there is only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the vehicleInfo object to the data at the column index in the cursor.
        assertEquals(testData.getAppId(), cursor.getInt(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID)));
        assertEquals(testData.getSessionID(), cursor.getString(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID)));
        assertEquals(testData.getJsonName(), cursor.getString(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME)));
        assertEquals(testData.getValueText(), cursor.getString(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_TEXT)));
        assertEquals(testData.getValueInt().longValue(), cursor.getLong(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_INT)));
        assertEquals(testData.getValueDouble().doubleValue(), cursor.getDouble(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_DOUBLE)));

        // Insert subtest 2.
        // Tests that we can't insert a record whose primary key value already exists.

        // Defines a ContentValues object so that the test can add an ID to it.
        ContentValues values = testData.getContentValues();

        // Adds the ID retrieved in subtest 1 to the ContentValues object.
        values.put(OnYardContract.DataPendingSync.COLUMN_NAME_ID, testDataId);

        // Tries to insert this record into the table. This should fail and drop into the
        // catch block. If it succeeds, issue a failure message.
        try {
            rowUri = mMockResolver.insert(OnYardContract.DataPendingSync.CONTENT_URI, values);
            fail("Expected insert failure for existing record but insert succeeded.");
        }
        catch (Exception e) {
            // succeeded, so do nothing.
        }
    }

    public void testBodyStyleSpecialtyInserts() {
        // Creates a new body style specialty instance
        BodyStyleSpecialtyInfo bodyStyleSpecialty = new BodyStyleSpecialtyInfo("78", "Body Style 78", false);

        // Insert subtest 1.
        // Inserts a row using the new body style instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.BodyStyleSpecialty.CONTENT_URI,  // the main table
                                                                             // URI
                bodyStyleSpecialty.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.BodyStyleSpecialty.CONTENT_URI,  // the main
                                                                                            // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int salvageTypeIndex = cursor.getColumnIndex(OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE);
        int nameIndex = cursor.getColumnIndex(OnYardContract.BodyStyleSpecialty.COLUMN_NAME_BODY_STYLE_NAME);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the body style object to the data at the column index in the cursor.
        assertEquals(bodyStyleSpecialty.getSalvageType(), cursor.getString(salvageTypeIndex));
        assertEquals(bodyStyleSpecialty.getBodyStyleName(), cursor.getString(nameIndex));
    }
    
    public void testLicensePlateConditionInserts() {
        // Creates a new license plate condition instance
        LicensePlateConditionInfo licensePlateCondition = new LicensePlateConditionInfo("L7", "License Plate Condition 7", false);

        // Insert subtest 1.
        // Inserts a row using the new license plate condition instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.LicensePlateCondition.CONTENT_URI,      // the main table
                                                                                 // URI
                licensePlateCondition.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.LicensePlateCondition.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int codeIndex = cursor.getColumnIndex(OnYardContract.LicensePlateCondition.COLUMN_NAME_CODE);
        int descIndex = cursor.getColumnIndex(OnYardContract.LicensePlateCondition.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the license [plate condition object to the data at the column index in the cursor.
        assertEquals(licensePlateCondition.getLicensePlateConditionCode(), cursor.getString(codeIndex));
        assertEquals(licensePlateCondition.getLicensePlateConditionDescription(), cursor.getString(descIndex));
    }

    public void testOdometerReadingTypeInserts() {
        // Creates a new odometer reading type instance
        OdometerReadingTypeInfo odometerReadingType = new OdometerReadingTypeInfo("78", "Odometer reading type 7", false);

        // Insert subtest 1.
        // Inserts a row using the new odometer reading type instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.OdometerReadingType.CONTENT_URI,    // the main table
                                                                                // URI
                odometerReadingType.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.OdometerReadingType.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int codeIndex = cursor.getColumnIndex(OnYardContract.OdometerReadingType.COLUMN_NAME_CODE);
        int descIndex = cursor.getColumnIndex(OnYardContract.OdometerReadingType.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the odometer reading type object to the data at the column index in the cursor.
        assertEquals(odometerReadingType.getOdometerReadingTypeCode(), cursor.getString(codeIndex));
        assertEquals(odometerReadingType.getOdometerReadingTypeDescription(), cursor.getString(descIndex));
    }

    public void testPublicVinInserts() {
        // Creates a new piblic VIN instance
        PublicVinInfo publicVIN = new PublicVinInfo("X", "VIN status X", false);

        // Insert subtest 1.
        // Inserts a row using the new public VIN instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.PublicVIN.CONTENT_URI,   // the main table
                                                                     // URI
                publicVIN.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.PublicVIN.CONTENT_URI,   // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int codeIndex = cursor.getColumnIndex(OnYardContract.PublicVIN.COLUMN_NAME_CODE);
        int descIndex = cursor.getColumnIndex(OnYardContract.PublicVIN.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the public VIN object to the data at the column index in the cursor.
        assertEquals(publicVIN.getPublicVinCode(), cursor.getString(codeIndex));
        assertEquals(publicVIN.getPublicVinDescription(), cursor.getString(descIndex));
    }

    public void testSalvageConditionInserts() {
        // Creates a new salvage condition instance
        SalvageConditionInfo salvageCondition = new SalvageConditionInfo("SC7", "Salvage condition 7", false);

        // Insert subtest 1.
        // Inserts a row using the new salvage condition instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SalvageCondition.CONTENT_URI,    // the main table
                                                                             // URI
                salvageCondition.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageCondition.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int codeIndex = cursor.getColumnIndex(OnYardContract.SalvageCondition.COLUMN_NAME_CODE);
        int descIndex = cursor.getColumnIndex(OnYardContract.SalvageCondition.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the salvage condition object to the data at the column index in the cursor.
        assertEquals(salvageCondition.getSalvageConditionCode(), cursor.getString(codeIndex));
        assertEquals(salvageCondition.getSalvageConditionDescription(), cursor.getString(descIndex));
    }

    public void testSalvageTypeInserts() {
        // Creates a new salvage type instance
        SalvageTypeInfo salvageType = new SalvageTypeInfo("Styp7", "Salvage type 7", false);

        // Insert subtest 1.
        // Inserts a row using the new salvage type instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SalvageType.CONTENT_URI,  // the main table
                                                                     // URI
                salvageType.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageType.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int typeIndex = cursor.getColumnIndex(OnYardContract.SalvageType.COLUMN_NAME_TYPE);
        int descIndex = cursor.getColumnIndex(OnYardContract.SalvageType.COLUMN_NAME_DESCRIPTION);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the salvage type object to the data at the column index in the cursor.
        assertEquals(salvageType.getSalvageType(), cursor.getString(typeIndex));
        assertEquals(salvageType.getSalvageDescription(), cursor.getString(descIndex));
    }

    public void testStateInserts() {
        // Creates a new state instance
        StateInfo state = new StateInfo("XY", "State XY", false);

        // Insert subtest 1.
        // Inserts a row using the new state instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.State.CONTENT_URI,      // the main table
                                                                    // URI
                state.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.State.CONTENT_URI,   // the main
                                                                                // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int abbrIndex = cursor.getColumnIndex(OnYardContract.State.COLUMN_NAME_ABBREVIATION);
        int nameIndex = cursor.getColumnIndex(OnYardContract.State.COLUMN_NAME_NAME);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the state object to the data at the column index in the cursor.
        assertEquals(state.getStateAbbr(), cursor.getString(abbrIndex));
        assertEquals(state.getStateName(), cursor.getString(nameIndex));
    }

    public void testCheckinFieldInserts() {
        CheckinFieldInfo checkinField = new CheckinFieldInfo(11, "String", "List", "Caption",
                "code", 12, true, 13, 14, 15, "member", false);

        // Insert subtest 1.
        // Inserts a row using the new instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.CheckinField.CONTENT_URI,  // the main table
                                                                      // URI
                checkinField.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int idIndex = cursor.getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_ID);
        int fieldtypeIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_FIELD_TYPE_DESCRIPTION);
        int inputTypeIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_INPUT_TYPE_DESCRIPTION);
        int captionIndex = cursor.getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_CAPTION);
        int codeIndex = cursor.getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_FEATURE_CODE);
        int groupNumIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_FEATURE_GROUP_NUMBER);
        int requiredIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_IS_REQUIRED);
        int minIntIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_MIN_INT_VALUE);
        int maxIntIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_MAX_INT_VALUE);
        int maxLenIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_MAX_STRING_LENGTH);
        int memberIndex = cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_DATA_MEMBER_NAME);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the object to the data at the column index in the cursor.
        assertEquals(checkinField.getId(), cursor.getInt(idIndex));
        assertEquals(checkinField.getFieldType(), cursor.getString(fieldtypeIndex));
        assertEquals(checkinField.getInputType(), cursor.getString(inputTypeIndex));
        assertEquals(checkinField.getCaption(), cursor.getString(captionIndex));
        assertEquals(checkinField.getFeatureCode(), cursor.getString(codeIndex));
        assertEquals(checkinField.getFeatureGroupNumber(), (Integer) cursor.getInt(groupNumIndex));
        assertEquals(checkinField.isRequired(), cursor.getInt(requiredIndex) == 1);
        assertEquals(checkinField.getMinIntValue(), (Integer) cursor.getInt(minIntIndex));
        assertEquals(checkinField.getMaxIntValue(), (Integer) cursor.getInt(maxIntIndex));
        assertEquals(checkinField.getMaxStringLength(), (Integer) cursor.getInt(maxLenIndex));
        assertEquals(checkinField.getDataMemberName(), cursor.getString(memberIndex));
    }

    public void testCheckinTemplateInserts() {
        CheckinTemplateInfo checkinTemplate = new CheckinTemplateInfo("1", 2, 3, false);

        // Insert subtest 1.
        // Inserts a row using the new instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.CheckinTemplate.CONTENT_URI, // the main table
                // URI
                checkinTemplate.getContentValues() // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI, // the main
                // table
                // URI
                null, // no projection, return all the columns
                null, // no selection criteria, return all the rows in the model
                null, // no selection arguments
                null // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int typeIndex = cursor
                .getColumnIndex(OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE);
        int fieldIndex = cursor
                .getColumnIndex(OnYardContract.CheckinTemplate.COLUMN_NAME_CHECKIN_FIELD_ID);
        int seqIndex = cursor.getColumnIndex(OnYardContract.CheckinTemplate.COLUMN_NAME_SEQUENCE);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the object to the data at the column index in the cursor.
        assertEquals(checkinTemplate.getSalvageType(), cursor.getString(typeIndex));
        assertEquals(checkinTemplate.getCheckinFieldId(), cursor.getInt(fieldIndex));
        assertEquals(checkinTemplate.getSequence(), cursor.getInt(seqIndex));
    }
    
    public void testSlaSalvageEnhancementInserts() {
        // Create a new SLA salvage enhancement instance
    	SlaSalvageEnhancementInfo slaSalvageEnhancement = new SlaSalvageEnhancementInfo("000-12345678", 1, 123, true, true, false);
    	
        // Insert subtest 1.
        // Inserts a row using the new SLA salvage enhancement instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SlaSalvageEnhancement.CONTENT_URI,  // the main table
                                                                     // URI
        		slaSalvageEnhancement.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SlaSalvageEnhancement.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int stockIndex = cursor.getColumnIndex(OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_STOCK_NUMBER);
        int cycleIndex = cursor.getColumnIndex(OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_CYCLE_ID);
        int enhIndex = cursor.getColumnIndex(OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID);
        int reqIndex = cursor.getColumnIndex(OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_IS_REQUIRED);
        int approvalIndex = cursor.getColumnIndex(OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_IS_APPROVAL_NEEDED);
        
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the SLA salvage enhancement object to the data at the column index in the cursor.
        assertEquals(slaSalvageEnhancement.getStockNumber(), cursor.getString(stockIndex));
        assertEquals(slaSalvageEnhancement.getVehicleCycleId(), cursor.getInt(cycleIndex));
        assertEquals(slaSalvageEnhancement.getEnhancementId(), cursor.getInt(enhIndex));
        assertEquals(slaSalvageEnhancement.isRequired() ? 1 : 0,  cursor.getInt(reqIndex));
        assertEquals(slaSalvageEnhancement.isApprovalNeeded() ? 1 : 0, cursor.getInt(approvalIndex));
    }


    public void testSalvageEnhancementInserts() {
        // Create a new salvage enhancement instance
    	SalvageEnhancementInfo salvageEnhancement = new SalvageEnhancementInfo("000-12345678", 123, "ABC", 123456, false);
    	
        // Insert subtest 1.
        // Inserts a row using the new salvage enhancement instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SalvageEnhancement.CONTENT_URI,  // the main table
                                                                     // URI
        		salvageEnhancement.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageEnhancement.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int stockIndex = cursor.getColumnIndex(OnYardContract.SalvageEnhancement.COLUMN_NAME_STOCK_NUMBER);
        int enhIndex = cursor.getColumnIndex(OnYardContract.SalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID);
        int statusIndex = cursor.getColumnIndex(OnYardContract.SalvageEnhancement.COLUMN_NAME_STATUS_CODE);
        int timeIndex = cursor.getColumnIndex(OnYardContract.SalvageEnhancement.COLUMN_NAME_UPDATE_DATETIME);
        		
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the SLA salvage enhancement object to the data at the column index in the cursor.
        assertEquals(salvageEnhancement.getStockNumber(), cursor.getString(stockIndex));
        assertEquals(salvageEnhancement.getEnhancementId(), cursor.getInt(enhIndex));
        assertEquals(salvageEnhancement.getStatusCode(), cursor.getString(statusIndex));
        assertEquals(salvageEnhancement.getUpdateDateTime(), cursor.getLong(timeIndex));
    }

    public void testEnhancementInserts() {
        // Create a new enhancement instance
    	EnhancementInfo enhancement = new EnhancementInfo(123, "Enhancement Description", false);
    	
        // Insert subtest 1.
        // Inserts a row using the new enhancement instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.Enhancement.CONTENT_URI,  // the main table
                                                                     // URI
        		enhancement.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.Enhancement.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int idIndex = cursor.getColumnIndex(OnYardContract.Enhancement.COLUMN_NAME_ID);
        int descIndex = cursor.getColumnIndex(OnYardContract.Enhancement.COLUMN_NAME_DESCRIPTION);
       		
        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the SLA salvage enhancement object to the data at the column index in the cursor.
        assertEquals(enhancement.getEnhancementDescription(), cursor.getString(descIndex));
    }
    
    public void testDisabledEnhancementInserts() {
        // Create a new disabled enhancement instance
    	DisabledEnhancementInfo disabledEEnhancement = new DisabledEnhancementInfo(123, false);
    	
        // Insert subtest 1.
        // Inserts a row using the new disabled enhancement instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.DisabledEnhancement.CONTENT_URI,  // the main table
                                                                     // URI
        		disabledEEnhancement.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.DisabledEnhancement.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());
    }

    public void testSyncWindowInserts() {
        // Create a new sync window instance
    	SyncWindowInfo syncWindow = new SyncWindowInfo(2, 600, 120, false);
    	
        // Insert subtest 1.
        // Inserts a row using the new sync window instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SyncWindow.CONTENT_URI,  // the main table
                                                                     // URI
        		syncWindow.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SyncWindow.CONTENT_URI, // the main
                                                                                    // table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());
    }

    public void testSyncWindowExceptionInserts() {
        // Create a new sync window exception instance
    	SyncWindowExceptionInfo syncWindowException = new SyncWindowExceptionInfo(123, false);
    	
        // Insert subtest 1.
        // Inserts a row using the new sync window exception instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        mMockResolver.insert(OnYardContract.SyncWindowException.CONTENT_URI,  // the main table
                                                                     // URI
        		syncWindowException.getContentValues()     // the map of values to insert as a new record
                );

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(OnYardContract.SyncWindowException.CONTENT_URI, // the main
                                                                                    		// table
                // URI
                null,                      // no projection, return all the columns
                null,                      // no selection criteria, return all the rows in the model
                null,                      // no selection arguments
                null                       // default sort order
                );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());
    }
    
    /*
     * Tests deletions from the data model.
     */
    public void testVehicleDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column to "Stock_Number"
        final String SELECTION_COLUMNS = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = "
                + "?";

        // Sets the selection argument "Stock0"
        final String[] SELECTION_ARGS = { "Stock0" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.Vehicles.CONTENT_URI, // the base URI
                                                                                    // of the table
            SELECTION_COLUMNS,         // select based on the stock number column
            SELECTION_ARGS             // select stock number = "Stock0"
        );

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row with stock number "Stock0"
        rowsDeleted = mMockResolver.delete(OnYardContract.Vehicles.CONTENT_URI, // the base URI of
                                                                                // the table
            SELECTION_COLUMNS,         // same selection column, "Stock_Number"
            SELECTION_ARGS             // same selection arguments, stock number = "Stock0"
        );

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.Vehicles.CONTENT_URI, // the base URI of
                                                                                 // the table
            null,                      // no projection, return all columns
            SELECTION_COLUMNS,         // select based on the stock number column
            SELECTION_ARGS,            // select stock number = "Stock0"
            null                       // use the default sort order
        );

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testConfigDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.Config.COLUMN_NAME_KEY + " = " + "?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "Key3" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.Config.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.Config.CONTENT_URI, SELECTION_COLUMNS,
                SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testImageCaptionDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.ImageCaption.COLUMN_NAME_CAPTION + " = "
                + "? AND " + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER + "=? AND "
                + OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "Caption1", "1", "1" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.ImageCaption.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.ImageCaption.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testImageTypeDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + " =" + "?";


        // Sets the selection argument
        final String[] SELECTION_ARGS = { "ImageType1" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.ImageType.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.ImageType.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }


    public void testImageReshootDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER
                + " = " + "?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { String.valueOf(3) };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.ImageReshoot.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.ImageReshoot.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testSaleDocTypeDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SaleDocType.COLUMN_NAME_ID + " = " + "?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { String.valueOf(3) };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SaleDocType.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SaleDocType.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SaleDocType.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testSalvageProviderDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SalvageProvider.COLUMN_NAME_ID + " = "
                + "?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { String.valueOf(3) };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SalvageProvider.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SalvageProvider.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageProvider.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testDataPendingSyncDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID
                + " = " + "?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "Session2" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.DataPendingSync.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.DataPendingSync.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testCheckinFieldDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.CheckinField.COLUMN_NAME_ID + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "4" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.CheckinField.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.CheckinField.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testCheckinTemplateDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.CheckinTemplate.COLUMN_NAME_CHECKIN_FIELD_ID
                + " = ? AND " + OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "2", "2" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.CheckinTemplate.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.CheckinTemplate.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.CheckinTemplate.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testSlaSalvageEnhancementDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                + " = ? AND " + OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "000-22222222", "222"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SlaSalvageEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SlaSalvageEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SlaSalvageEnhancement.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }
    
    public void testSalvageEnhancementDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                + " = ? AND " + OnYardContract.SalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = { "000-11111111", "222"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SalvageEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SalvageEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SalvageEnhancement.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }
   
    public void testEnhancementDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.Enhancement.COLUMN_NAME_ID + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = {"22"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.Enhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.Enhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.Enhancement.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }
   
    public void testDisabledEnhancementDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.DisabledEnhancement.COLUMN_NAME_ID + " = ?";

        // Sets the selection argument
        final String[] SELECTION_ARGS = {"11"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.DisabledEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.DisabledEnhancement.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.DisabledEnhancement.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testSyncWindowDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + " = ? AND "
        + OnYardContract.SyncWindow.COLUMN_NAME_START_TIME + " = ?";
        
        // Sets the selection argument
        final String[] SELECTION_ARGS = {"2", "200"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SyncWindow.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SyncWindow.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SyncWindow.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    public void testSyncWindowExceptionDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column
        final String SELECTION_COLUMNS = OnYardContract.SyncWindowException.COLUMN_NAME_SALVAGE_PROVIDER_ID + " = ?";
        
        // Sets the selection argument
        final String[] SELECTION_ARGS = {"111"};

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(OnYardContract.SyncWindowException.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row
        rowsDeleted = mMockResolver.delete(OnYardContract.SyncWindowException.CONTENT_URI,
                SELECTION_COLUMNS, SELECTION_ARGS);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(OnYardContract.SyncWindowException.CONTENT_URI, null,
                SELECTION_COLUMNS, SELECTION_ARGS, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    /*
     * Tests updates to the data model.
     */
    public void testVehicleUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = "
                + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { "Stock1" };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        // Sets up the update by putting the "Branch_Number" column and a value into the values map.
        values.put(OnYardContract.Vehicles.COLUMN_NAME_AISLE, "BK");

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.Vehicles.CONTENT_URI,  // the URI of
                                                                                    // the data
                                                                                    // table
            values,                     // a map of the updates to do (column stock number and value)
            SELECTION_COLUMNS,           // select based on the stock number column
            selectionArgs               // select "stock_number = Stock1"
        );

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        //  Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.Vehicles.CONTENT_URI,   // The URI of the
                                                                                // data table
            values,                      // the same map of updates
            SELECTION_COLUMNS,            // same selection, based on the stock number column
            selectionArgs                // same selection argument, to select "stock_Number = Stock1"
        );

        // Asserts that only one row was updated. The selection criteria evaluated to
        // "stock_Number = Stock1", and the test data should only contain one row that matches that.
        assertEquals(1, rowsUpdated);
        
        rowsUpdated = mMockResolver.update(Uri.withAppendedPath(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock2"),
        	values,
        	null,
        	null
        );
        
        Cursor cursor = mMockResolver.query(Uri.withAppendedPath(
                OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock2"),
                new String[] { OnYardContract.Vehicles.COLUMN_NAME_AISLE },
            null,
            null,
            null
        );
        
        cursor.moveToFirst();
        
        assertEquals("BK", cursor.getString(0));
    }
    
    public void testConfigUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.Config.COLUMN_NAME_KEY + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { "Key4" };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String updatedValue = "UpdatedValue";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, updatedValue);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.Config.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.Config.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.Config.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(updatedValue,
                cursor.getString(cursor.getColumnIndex(OnYardContract.Config.COLUMN_NAME_VALUE)));
    }

    public void testImageCaptionUpdates() {
         // Selection column for identifying a record in the data model.
         final String SELECTION_COLUMNS = OnYardContract.ImageCaption.COLUMN_NAME_ID + " = " +
         "?";
        
         // Selection argument for the selection column.
         final String[] selectionArgs = { String.valueOf(3) };
        
         // Defines a map of column names and values
         ContentValues values = new ContentValues();
        
         // Subtest 1.
         // Tries to update a record in an empty table.
        
         String updatedCaption = "UpdatedValue";
         // Sets up the update by putting a value into the values map.
         values.put(OnYardContract.ImageCaption.COLUMN_NAME_CAPTION, updatedCaption);
        
         // Tries to update the table
         int rowsUpdated = mMockResolver.update(OnYardContract.ImageCaption.CONTENT_URI, values,
         SELECTION_COLUMNS, selectionArgs);
        
         // Asserts that no rows were updated.
         assertEquals(0, rowsUpdated);
        
         // Subtest 2.
         // Builds the table, and then tries the update again using the same arguments.
        
         // Inserts data into the model.
         insertData();
        
         // Does the update again, using the same arguments as in subtest 1.
         rowsUpdated = mMockResolver.update(OnYardContract.ImageCaption.CONTENT_URI, values,
         SELECTION_COLUMNS, selectionArgs);
        
         // Asserts that only one row was updated.
         assertEquals(1, rowsUpdated);
        
         Cursor cursor = mMockResolver.query(OnYardContract.ImageCaption.CONTENT_URI, null,
         SELECTION_COLUMNS, selectionArgs, null);
        
         cursor.moveToFirst();
        
         assertEquals(updatedCaption, cursor.getString(cursor
         .getColumnIndex(OnYardContract.ImageCaption.COLUMN_NAME_CAPTION)));
    }

    public void testImageTypeUpdates() {
         // Selection column for identifying a record in the data model.
         final String SELECTION_COLUMNS = OnYardContract.ImageType.COLUMN_NAME_ID + " = " +
         "?";
        
         // Selection argument for the selection column.
         final String[] selectionArgs = { String.valueOf(3) };
        
         // Defines a map of column names and values
         ContentValues values = new ContentValues();
        
         // Subtest 1.
         // Tries to update a record in an empty table.
        
         String updatedType = "UpdatedValue";
         // Sets up the update by putting a value into the values map.
         values.put(OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME, updatedType);
        
         // Tries to update the table
         int rowsUpdated = mMockResolver.update(OnYardContract.ImageType.CONTENT_URI, values,
         SELECTION_COLUMNS, selectionArgs);
        
         // Asserts that no rows were updated.
         assertEquals(0, rowsUpdated);
        
         // Subtest 2.
         // Builds the table, and then tries the update again using the same arguments.
        
         // Inserts data into the model.
         insertData();
        
         // Does the update again, using the same arguments as in subtest 1.
         rowsUpdated = mMockResolver.update(OnYardContract.ImageType.CONTENT_URI, values,
         SELECTION_COLUMNS, selectionArgs);
        
         // Asserts that only one row was updated.
         assertEquals(1, rowsUpdated);
        
         Cursor cursor = mMockResolver.query(OnYardContract.ImageType.CONTENT_URI, null,
         SELECTION_COLUMNS, selectionArgs, null);
        
         cursor.moveToFirst();
        
         assertEquals(updatedType, cursor.getString(cursor
         .getColumnIndex(OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME)));
    }


    
    public void testDataPendingSyncUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME
                + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { "JsonName5" };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String udpatedTextValue = "UpdatedValue";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_TEXT, udpatedTextValue);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.DataPendingSync.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.DataPendingSync.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(udpatedTextValue, cursor.getString(cursor
                .getColumnIndex(OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_TEXT)));
    }

    public void testImageReshootUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER
                + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { String.valueOf(4) };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String newStockNumber = "StockNumberNew";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER, newStockNumber);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.ImageReshoot.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.ImageReshoot.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.ImageReshoot.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(newStockNumber, cursor.getString(cursor
                .getColumnIndex(OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER)));
    }

    public void testSaleDocTypeUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.SaleDocType.COLUMN_NAME_ID + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { String.valueOf(4) };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String newDesc = "New Sale Doc Description";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.SaleDocType.COLUMN_NAME_DESCRIPTION, newDesc);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.SaleDocType.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.SaleDocType.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.SaleDocType.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(newDesc, cursor.getString(cursor
                .getColumnIndex(OnYardContract.SaleDocType.COLUMN_NAME_DESCRIPTION)));
    }

    public void testSalvageProviderUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.SalvageProvider.COLUMN_NAME_ID + " = "
                + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { String.valueOf(4) };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String newDesc = "New Salvage Provider Name";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.SalvageProvider.COLUMN_NAME_DESCRIPTION, newDesc);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.SalvageProvider.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.SalvageProvider.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.SalvageProvider.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(newDesc, cursor.getString(cursor
                .getColumnIndex(OnYardContract.SalvageProvider.COLUMN_NAME_DESCRIPTION)));
    }

    public void testCheckinFieldUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYardContract.CheckinField.COLUMN_NAME_ID + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { String.valueOf(4) };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        String newDesc = "New Checkin Field caption";
        // Sets up the update by putting a value into the values map.
        values.put(OnYardContract.CheckinField.COLUMN_NAME_CAPTION, newDesc);

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(OnYardContract.CheckinField.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(OnYardContract.CheckinField.CONTENT_URI, values,
                SELECTION_COLUMNS, selectionArgs);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);

        Cursor cursor = mMockResolver.query(OnYardContract.CheckinField.CONTENT_URI, null,
                SELECTION_COLUMNS, selectionArgs, null);

        cursor.moveToFirst();

        assertEquals(newDesc, cursor.getString(cursor
                .getColumnIndex(OnYardContract.CheckinField.COLUMN_NAME_CAPTION)));
    }

    public void testVehicleInsertWithNullStockNum()
    {
        VehicleInfo vehicle = new VehicleInfo(null, "VIN0", "Claim0", 10, 20, "A", 30, "Col0", 40,
                "Make0", "Model0", 50, "St0", "D0", 0, false, 50, false, 500, 111, false, "L00", 40, 444, 1412928000,151200000);
    	        
        try {
            mMockResolver.insert(OnYardContract.Vehicles.CONTENT_URI,
                    vehicle.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullColorCode()
    {
        ColorInfo color = new ColorInfo(null, "Desc0", false);
    	        
        try {
            mMockResolver.insert(OnYardContract.Color.CONTENT_URI,
                    color.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullColorDesc()
    {
        ColorInfo color = new ColorInfo("Code0", null, false);
    	        
        try {
            mMockResolver.insert(OnYardContract.Color.CONTENT_URI,
                    color.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullStatusCode()
    {
    	StatusInfo status = new StatusInfo(null, "Desc0");
    	        
        try {
            mMockResolver.insert(OnYardContract.Status.CONTENT_URI,
                    status.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullStatusDesc()
    {
    	StatusInfo status = new StatusInfo("Code0", null);
    	        
        try {
            mMockResolver.insert(OnYardContract.Status.CONTENT_URI,
                    status.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullDamageCode()
    {
        DamageInfo damage = new DamageInfo(null, "Desc0", false);
    	        
        try {
            mMockResolver.insert(OnYardContract.Damage.CONTENT_URI,
                    damage.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testVehicleInsertWithNullDamageDesc()
    {
        DamageInfo damage = new DamageInfo("Code0", null, false);
    	        
        try {
            mMockResolver.insert(OnYardContract.Damage.CONTENT_URI,
                    damage.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateStockNumVehicleInserts()
    {
    	VehicleInfo[] TEST_VEHICLES_DUPLICATE_STOCK_NUMBER = {
                new VehicleInfo("Stock1", "VIN0", "Claim0", 10, 20, "A", 30, "Col0", 40, "Make0",
                        "Model0", 50, "St0", "D0", 0, false, 50, false, 500, 111, false, "L40", 40, 555, 1412928000,144000000),
                new VehicleInfo("Stock1", "VIN1", "Claim1", 11, 21, "B", 31, "Col1", 41, "Make1",
                        "Model1", 51, "St1", "D1", 0, false, 51, true, 600, 111, false, "L41", 50, 555, 1412928000,144000000) };
    	
        mMockResolver.insert(OnYardContract.Vehicles.CONTENT_URI,
                TEST_VEHICLES_DUPLICATE_STOCK_NUMBER[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(OnYardContract.Vehicles.CONTENT_URI,
                    TEST_VEHICLES_DUPLICATE_STOCK_NUMBER[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
 
    public void testDuplicateImageTypeInserts()
    {
        ImageTypeInfo[] TEST_IMAGE_TYPE_DUPLICATE_CODE = {
                new ImageTypeInfo(11, "ImageType1", false),
                new ImageTypeInfo(11, "ImageType2", false) };
        
        mMockResolver.insert(OnYardContract.ImageType.CONTENT_URI,
                TEST_IMAGE_TYPE_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(OnYardContract.ImageType.CONTENT_URI,
                    TEST_IMAGE_TYPE_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    
    public void testDuplicateColorCodeInserts()
    {
        ColorInfo[] TEST_COLOR_DUPLICATE_CODE = { new ColorInfo("BL", "Black", false),
                new ColorInfo("BL", "Green", false) };
    	
        mMockResolver.insert(OnYardContract.Color.CONTENT_URI,
                TEST_COLOR_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(OnYardContract.Color.CONTENT_URI,
                    TEST_COLOR_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateDamageCodeInserts()
    {
        DamageInfo[] TEST_DAMAGE_DUPLICATE_CODE = { new DamageInfo("ABC", "Black", false),
                new DamageInfo("ABC", "Green", false) };
    	
        mMockResolver.insert(OnYardContract.Damage.CONTENT_URI,
                TEST_DAMAGE_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(OnYardContract.Damage.CONTENT_URI,
                    TEST_DAMAGE_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateStatusCodeInserts()
    {
    	StatusInfo[] TEST_STATUS_DUPLICATE_CODE = {
                new StatusInfo("BL", "Black"),
                new StatusInfo("BL", "Green")};
    	
        mMockResolver.insert(OnYardContract.Status.CONTENT_URI,
                TEST_STATUS_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(OnYardContract.Status.CONTENT_URI,
                    TEST_STATUS_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }

    public void testDuplicateSaleDocTypeIdInserts() {
        SaleDocTypeInfo[] TEST_SALE_DOC_TYPE_DUPLICATE_ID = {
                new SaleDocTypeInfo(111, "Sale Doc 1"), new SaleDocTypeInfo(111, "Sale Doc 2") };

        mMockResolver.insert(OnYardContract.SaleDocType.CONTENT_URI,
                TEST_SALE_DOC_TYPE_DUPLICATE_ID[0].getContentValues());

        try {
            mMockResolver.insert(OnYardContract.SaleDocType.CONTENT_URI,
                    TEST_SALE_DOC_TYPE_DUPLICATE_ID[1].getContentValues());

            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        }
        catch (Exception e) {
            // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateBranchNumInserts() {
        BranchInfo[] TEST_BRANCH_DUPLICATE_NUM = {
                new BranchInfo("666", "Branch 61", "S6","PT", false), 
                new BranchInfo("666", "Branch 62", "S6","PT", false) };

        mMockResolver.insert(OnYardContract.Branch.CONTENT_URI,
                TEST_BRANCH_DUPLICATE_NUM[0].getContentValues());

        try {
            mMockResolver.insert(OnYardContract.Branch.CONTENT_URI,
                    TEST_BRANCH_DUPLICATE_NUM[1].getContentValues());

            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        }
        catch (Exception e) {
            // succeeded, so do nothing.
        }
    }

    
}
