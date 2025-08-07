package com.daelim.sfa;

import com.daelim.sfa.domain.player.Player;
import com.daelim.sfa.domain.player.Position;
import com.daelim.sfa.repository.player.PlayerRepository;
import com.daelim.sfa.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class SfaApplicationTests {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PlayerService playerService;
	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	//@Rollback(false)
	//한 트랜젹션 안에서 조회, 변경이 일이나야 변경감지 동작 가능
	//단건 조회는 서비스 메서드 통해서 하지만
	//배치 작업은 여러개를 UPDATE해야 해서 배치 작업 메서드에 @Transactional 붙임
	void 변경감지_테스트(){
		Player player = playerRepository.findById(5L);
		player.updateTeamAndPosition(null, Position.Defender);
		//플러시하면 UPDATE 로그는 뜨는데 DB엔 반영 안됨
		//playerRepository.flush();
	}

}
