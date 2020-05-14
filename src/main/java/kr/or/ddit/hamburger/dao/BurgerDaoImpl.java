package kr.or.ddit.hamburger.dao;

import org.apache.ibatis.session.SqlSession;

import kr.or.ddit.db.mybatis.MybatisConfig;
import kr.or.ddit.hamburger.model.BurgerStoreVo;

public class BurgerDaoImpl implements BurgerDao{

	@Override
	public int insertBurgerStore(BurgerStoreVo burgerStoreVo) {
		
		SqlSession sqlSession = MybatisConfig.getSqlSession();
		int insertCnt = sqlSession.insert("insertBurgerStore", burgerStoreVo);
		sqlSession.commit();
		sqlSession.close();
		
		return insertCnt;
	}

}
