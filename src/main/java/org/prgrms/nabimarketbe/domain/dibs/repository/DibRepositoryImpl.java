package org.prgrms.nabimarketbe.domain.dibs.repository;

import static org.prgrms.nabimarketbe.domain.card.entity.QCard.*;
import static org.prgrms.nabimarketbe.domain.dibs.entity.QDib.*;

import java.util.List;

import org.prgrms.nabimarketbe.domain.dibs.dto.response.DibListReadPagingResponseDTO;
import org.prgrms.nabimarketbe.domain.dibs.dto.response.DibListReadResponseDTO;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DibRepositoryImpl implements DibRepositoryCustom{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public DibListReadPagingResponseDTO getUserDibsByUserId(
		Long userId,
		Long cursorId,
		Integer size
	) {
		List<DibListReadResponseDTO> dibList = jpaQueryFactory.select(
			Projections.fields(
				DibListReadResponseDTO.class,
				dib.dibId,
				card.cardId,
				card.cardTitle,
				card.item.itemName,
				card.item.priceRange,
				card.thumbNailImage.as("thumbNail")
			)
		)
			.from(dib)
			.join(card).on(card.cardId.eq(dib.card.cardId))
			.where(
				dibUserIdEquals(userId),
				greaterThan(cursorId)
			)
			.limit(size)
			.fetch();

		Long nextCursorId = generateCursor(dibList, size);

		return DibListReadPagingResponseDTO.of(dibList, nextCursorId);
	}

	private Long generateCursor(
		List<DibListReadResponseDTO> dibList,
		Integer size
	) {
		if (dibList.size() == size) {
			return dibList.get(dibList.size() - 1).getDibId();
		}

		return null;
	}

	private BooleanExpression dibUserIdEquals(Long userId) {
		if(userId == null) {
			return null;
		}

		return dib.user.userId.eq(userId);
	}

	private BooleanExpression greaterThan(Long cursorId) {
		if(cursorId == null) {
			return null;
		}

		return dib.dibId.gt(cursorId);
	}
}