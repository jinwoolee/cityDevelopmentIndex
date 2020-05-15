package kr.or.ddit.hamburger.dao;

import org.apache.ibatis.session.SqlSession;

import kr.or.ddit.db.mybatis.MybatisConfig;
import kr.or.ddit.hamburger.model.BurgerStoreVo;

public class BurgerDaoImpl implements BurgerDao{

	/** 
	 * Method   : insertBurgerStore
	 * 작성자 : jw
	 * 변경이력 : 
	 * @param burgerStoreVo
	 * @return 
	 * Method 설명 : 점포 입력 
	 */
	@Override
	public int insertBurgerStore(BurgerStoreVo burgerStoreVo) {
		
		SqlSession sqlSession = MybatisConfig.getSqlSession();
		int insertCnt = sqlSession.insert("insertBurgerStore", burgerStoreVo);
		sqlSession.commit();
		sqlSession.close();
		
		return insertCnt;
	}

	/** 
	 * Method   : deleteBurgerKing
	 * 작성자 : jw
	 * 변경이력 :  
	 * Method 설명 : 점포 삭제
	 */
	@Override
	public int deleteBurgerStore(String burgerKing) {
		SqlSession sqlSession = MybatisConfig.getSqlSession();
		int deleteCnt = sqlSession.delete("deleteBurgerStore", burgerKing);
		sqlSession.commit();
		sqlSession.close();
		
		return deleteCnt;
		
	}

}
