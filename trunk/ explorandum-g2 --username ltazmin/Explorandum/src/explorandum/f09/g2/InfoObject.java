package explorandum.f09.g2;

public class InfoObject {
	
	private boolean boolField = false;
	
	private Object objField = null;

	/**
	 * @return the boolField
	 */
	public boolean isBoolField() {
		return boolField;
	}

	/**
	 * @return the objField
	 */
	public Object getObjField() {
		return objField;
	}

	/**
	 * @param boolField_
	 * @param objField_
	 */
	public InfoObject(boolean boolField_, Object objField_) {
		super();
		boolField = boolField_;
		objField = objField_;
	}
	
}
