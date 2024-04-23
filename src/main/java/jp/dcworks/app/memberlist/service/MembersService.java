package jp.dcworks.app.memberlist.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import jp.dcworks.app.memberlist.dto.RequestMember;
import jp.dcworks.app.memberlist.entity.Members;
import jp.dcworks.app.memberlist.repository.MembersRepository;
import lombok.extern.log4j.Log4j2;

/**
 * メンバー関連サービスクラス。
 *
 * @author tomo-sato
 */
@Log4j2
@Service
public class MembersService {

	/** リポジトリインターフェース。 */
	@Autowired
	private MembersRepository repository;

	/**
	 * メンバー全件取得する。
	 *
	 * @return メンバーを全件取得する。
	 */
	public Page<Members> findAllMembers(Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.ASC, "id"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(orderList));

		return repository.findAll(pageable);
	}

	/**
	 * メンバー全件取得する。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return メンバーを全件取得する。
	 */
	public Page<Members> findAllMembers(Long sinceId, Integer page) {
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(Direction.ASC, "id"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(orderList));

		return repository.findByIdGreaterThan(sinceId, pageable);
	}

	/**
	 * メンバー全件取得する。
	 *
	 * @return メンバーを全件取得する。
	 */
	public List<Members> findAllMembers() {
		return repository.findAllByOrderByIdAsc();
	}

	/**
	 * メンバー一覧を取得する。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return 指定したIDよりも新しいデータを10件取得する。
	 */
	public List<Members> findMembers(Integer sinceId) {
		return repository.findFirst10ByIdGreaterThan(sinceId);
	}

	/**
	 * メンバー検索を行う。
	 * メンバーIDを指定し、メンバーを検索する。
	 *
	 * @param membersId メンバーID
	 * @return メンバー情報を返す。
	 */
	public Members findById(Long membersId) {
		log.info("メンバーを検索します。：membersId={}", membersId);

		Members members = repository.findById(membersId).orElse(null);
		log.info("メンバー検索結果。：usersId={}, members={}", membersId, members);

		return members;
	}

	/**
	 * メンバー検索を行う。
	 * メールアドレスを指定し、メンバーを検索する。
	 *
	 * @param emailAddress メールアドレス
	 * @return メンバー情報を返す。
	 */
	public List<Members> findByEmailAddress(String emailAddress) {
		log.info("メンバーを検索します。：emailAddress={}", emailAddress);

		List<Members> membersList = repository.findByEmailAddress(emailAddress);
		log.info("メンバー検索結果。：emailAddress={}, membersList={}", emailAddress, membersList);

		return membersList;
	}

	/**
	 * メンバー登録処理を行う。
	 *
	 * @param requestMember メンバーDTO
	 */
	public void save(RequestMember requestMember) {
		Members members = new Members();
		members.setName(requestMember.getName());
		members.setEmailAddress(requestMember.getEmailAddress());
		repository.save(members);
	}

	/**
	 * メンバー登録処理を行う。
	 *
	 * @param members メンバーエンティティ
	 */
	public void save(Members members) {
		repository.save(members);
	}

	/**
	 * メンバー一覧を取得する。
	 * @return メンバー一覧
	 */
	public Iterable<Members> findAll() {
		return repository.findAll();
	}

	/**
	 * メンバーを削除する。
	 */
	public void delete(Members members) {
		repository.delete(members);
	}
}
