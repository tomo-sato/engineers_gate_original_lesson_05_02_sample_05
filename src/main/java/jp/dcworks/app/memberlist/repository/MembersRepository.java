package jp.dcworks.app.memberlist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import jp.dcworks.app.memberlist.entity.Members;

/**
 * メンバー関連リポジトリインターフェース。
 *
 * @author tomo-sato
 */
public interface MembersRepository extends PagingAndSortingRepository<Members, Long>, CrudRepository<Members, Long> {

	/**
	 * メンバー一覧を取得する。
	 * メンバーIDの昇順。
	 * @return メンバー一覧を返す。
	 */
	List<Members> findAllByOrderByIdAsc();

	/**
	 * メンバー検索を行う。
	 * メールアドレスを指定し、メンバーを検索する。
	 *
	 * @param emailAddress メールアドレス
	 * @return メンバー情報を返す。
	 */
	List<Members> findByEmailAddress(String emailAddress);

	/**
	 * メンバー一覧を取得する。
	 * メンバーIDの昇順。
	 * @return メンバー一覧を返す。
	 */
	Page<Members> findAll(Pageable pageable);

	/**
	 * メンバー一覧を取得する。
	 * メンバーIDの昇順。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return メンバー一覧を返す。
	 */
	Page<Members> findByIdGreaterThan(@Param("id") Long sinceId, Pageable pageable);

	/**
	 * メンバーを削除する。
	 */
	void delete(Members members);

	/**
	 * メンバー一覧を取得する。
	 *
	 * @param sinceId 指定されたIDよりも新しいものを取得する。
	 * @return 指定したIDよりも新しいデータを10件取得する。
	 */
	List<Members> findFirst10ByIdGreaterThan(@Param("id") Integer sinceId);
}
