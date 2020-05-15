package kr.or.ddit.hamburger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BurgerStoreVo {

	@SerializedName("region_1depth_name")	
	private String sido;
	
	@SerializedName("region_2depth_name")
	private String sigungu;
	
	@Expose(deserialize = true)
	private String storeCategory;
	
	@Expose(deserialize = true)
	private String storeName;
	
	@SerializedName("x")
	private double posX;
	
	@SerializedName("y")
	private double posY;
	
	public BurgerStoreVo() {
		
	}
	
	public BurgerStoreVo(String sido, String sigungu, String storeCategory, String storeName, double posX, double posY) {
		this.sido = sido;
		this.sigungu = sigungu;
		this.storeCategory = storeCategory;
		this.storeName = storeName;
		this.posX = posX;
		this.posY = posY;
	}
	
	public String getSido() {
		return sido;
	}
	public void setSido(String sido) {
		this.sido = sido;
	}
	public String getSigungu() {
		return sigungu;
	}
	public void setSigungu(String sigungu) {
		this.sigungu = sigungu;
	}
	public String getStoreCategory() {
		return storeCategory;
	}
	public void setStoreCategory(String storeCategory) {
		this.storeCategory = storeCategory;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public double getPosX() {
		return posX;
	}
	public void setPosX(double posX) {
		this.posX = posX;
	}
	public double getPosY() {
		return posY;
	}
	public void setPosY(double posY) {
		this.posY = posY;
	}
	@Override
	public String toString() {
		return "BurgerStoreVo [sido=" + sido + ", sigungu=" + sigungu + ", storeCategory=" + storeCategory + ", storeName=" + storeName + ", posX=" + posX + ", posY=" + posY + "]";
	}
	
	
}
