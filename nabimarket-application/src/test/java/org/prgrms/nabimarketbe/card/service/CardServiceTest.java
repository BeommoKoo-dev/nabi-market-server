package org.prgrms.nabimarketbe.card.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgrms.nabimarketbe.card.dto.request.CardCreateRequestDTO;
import org.prgrms.nabimarketbe.card.dto.request.CardStatusUpdateRequestDTO;
import org.prgrms.nabimarketbe.card.dto.request.CardUpdateRequestDTO;
import org.prgrms.nabimarketbe.card.dto.response.CardCreateResponseDTO;
import org.prgrms.nabimarketbe.card.dto.response.CardDetailResponseDTO;
import org.prgrms.nabimarketbe.card.dto.response.CardUpdateResponseDTO;
import org.prgrms.nabimarketbe.card.dto.response.wrapper.CardResponseDTO;
import org.prgrms.nabimarketbe.card.dto.response.wrapper.CardUserResponseDTO;
import org.prgrms.nabimarketbe.card.entity.Card;
import org.prgrms.nabimarketbe.card.entity.CardStatus;
import org.prgrms.nabimarketbe.card.entity.TradeType;
import org.prgrms.nabimarketbe.card.repository.CardRepository;
import org.prgrms.nabimarketbe.cardImage.entity.CardImage;
import org.prgrms.nabimarketbe.cardImage.repository.CardImageBatchRepository;
import org.prgrms.nabimarketbe.cardImage.repository.CardImageRepository;
import org.prgrms.nabimarketbe.cardimage.dto.request.CardImageCreateRequestDTO;
import org.prgrms.nabimarketbe.cardimage.dto.response.CardImageCreateResponseDTO;
import org.prgrms.nabimarketbe.cardimage.dto.response.CardImageSingleReadResponseDTO;
import org.prgrms.nabimarketbe.cardimage.dto.response.CardImageUpdateResponseDTO;
import org.prgrms.nabimarketbe.category.entity.Category;
import org.prgrms.nabimarketbe.category.entity.CategoryEnum;
import org.prgrms.nabimarketbe.category.repository.CategoryRepository;
import org.prgrms.nabimarketbe.item.entity.Item;
import org.prgrms.nabimarketbe.item.entity.PriceRange;
import org.prgrms.nabimarketbe.item.repository.ItemRepository;
import org.prgrms.nabimarketbe.user.dto.response.UserSummaryResponseDTO;
import org.prgrms.nabimarketbe.user.entity.User;
import org.prgrms.nabimarketbe.user.repository.UserRepository;
import org.prgrms.nabimarketbe.user.service.CheckService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @InjectMocks
    private CardService cardService;

    @Mock
    private CheckService checkService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CardImageRepository cardImageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardImageBatchRepository cardImageBatchRepository;

    private static String token;
    private static Long userId;
    private static Long cardId;
    private static LocalDateTime createdDate;
    private static LocalDateTime modifiedDate;
    private static User user;
    private static Category category;
    private static Item item;
    private static Card card;
    private static List<CardImage> images;

    @BeforeEach
    void setUp() {
        token = "token";
        userId = 1L;
        cardId = 1L;
        createdDate = LocalDateTime.now();
        modifiedDate = LocalDateTime.now();

        user = User.builder()
            .role("USER")
            .provider("GOOGLE")
            .nickname("doby")
            .imageUrl("img_url")
            .accountId("1010110")
            .build();
        ReflectionTestUtils.setField(user, "userId", userId);
        category = new Category(CategoryEnum.ELECTRONICS);
        item = Item.builder()
            .priceRange(PriceRange.PRICE_RANGE_FOUR)
            .itemName("apple macbook pro")
            .category(category)
            .build();
        card = Card.builder()
            .cardTitle("macbook pro 교환 원해요")
            .content("상태 최상입니다.")
            .tradeType(TradeType.DIRECT_DEALING)
            .pokeAvailable(true)
            .tradeArea("서울")
            .thumbnail("thumbnail_img_url")
            .user(user)
            .item(item)
            .build();
        ReflectionTestUtils.setField(card, "cardId", cardId);
        ReflectionTestUtils.setField(card, "createdDate", createdDate);
        ReflectionTestUtils.setField(card, "modifiedDate", modifiedDate);

        images = Arrays.asList(
            new CardImage("url1", card),
            new CardImage("url2", card),
            new CardImage("url3", card)
        );
    }

    @Test
    @DisplayName("유저가 새로운 카드를 생성한다.")
    void createCard() {
        // given
        CardCreateRequestDTO createRequest = new CardCreateRequestDTO(
            "macbook pro 교환 원해요",
            "thumbnail_img_url",
            "apple macbook pro",
            PriceRange.PRICE_RANGE_FOUR,
            TradeType.DIRECT_DEALING,
            "서울",
            CategoryEnum.ELECTRONICS,
            true,
            "상태 최상입니다.",
            Arrays.asList(
                new CardImageCreateRequestDTO("url1"),
                new CardImageCreateRequestDTO("url2"),
                new CardImageCreateRequestDTO("url3")
            )
        );

        when(checkService.parseToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(categoryRepository.findCategoryByCategoryName(createRequest.category())).thenReturn(Optional.of(category));
        when(itemRepository.save(any())).thenReturn(item);
        when(cardRepository.save(any())).thenReturn(card);

        CardResponseDTO<CardCreateResponseDTO> expectCardResponse = new CardResponseDTO<>(
            new CardCreateResponseDTO(
                cardId,
                "macbook pro 교환 원해요",
                "thumbnail_img_url",
                "apple macbook pro",
                PriceRange.PRICE_RANGE_FOUR,
                TradeType.DIRECT_DEALING,
                CategoryEnum.ELECTRONICS,
                "서울",
                true,
                "상태 최상입니다.",
                0,
                0,
                createdDate,
                modifiedDate,
                Arrays.asList(
                    new CardImageCreateResponseDTO("url1"),
                    new CardImageCreateResponseDTO("url2"),
                    new CardImageCreateResponseDTO("url3")
                )
            )
        );

        // when
        CardResponseDTO<CardCreateResponseDTO> actualCardResponse = cardService.createCard(token, createRequest);

        // then
        assertThat(actualCardResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectCardResponse);
    }

    @Test
    @DisplayName("유저가 자신의 카드 정보를 수정한다.")
    void updateCardById() {
        // given
        CardUpdateRequestDTO updateRequest = new CardUpdateRequestDTO(
            "galaxy book 교환 원해요",
            "thumbnail_img_url",
            "galaxy book",
            PriceRange.PRICE_RANGE_FIVE,
            TradeType.SHIPPING,
            CategoryEnum.ELECTRONICS,
            "서울",
            true,
            "상태 최상입니다.",
            Arrays.asList(
                new CardImageCreateRequestDTO("url1"),
                new CardImageCreateRequestDTO("url2"),
                new CardImageCreateRequestDTO("url3")
            )
        );

        when(checkService.parseToken(token)).thenReturn(userId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findActiveCardById(cardId)).thenReturn(Optional.of(card));
        when(checkService.isEqual(userId, card.getUser().getUserId())).thenReturn(true);
        when(categoryRepository.findCategoryByCategoryName(updateRequest.category())).thenReturn(Optional.of(category));

        CardResponseDTO<CardUpdateResponseDTO> expectedResponse = new CardResponseDTO<>(
            new CardUpdateResponseDTO(
                cardId,
                "galaxy book 교환 원해요",
                "thumbnail_img_url",
                "galaxy book",
                PriceRange.PRICE_RANGE_FIVE,
                TradeType.SHIPPING,
                CategoryEnum.ELECTRONICS,
                "서울",
                true,
                "상태 최상입니다.",
                0,
                0,
                createdDate,
                modifiedDate,
                Arrays.asList(
                    new CardImageUpdateResponseDTO("url1"),
                    new CardImageUpdateResponseDTO("url2"),
                    new CardImageUpdateResponseDTO("url3")
                )
            )
        );

        // when
        CardResponseDTO<CardUpdateResponseDTO> actualResponse = cardService.updateCardById(
            token,
            cardId,
            updateRequest
        );

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("특정 카드에 대한 정보를 조회한다.")
    void getCardById() {
        // given
        String token = null;

        when(cardRepository.findActiveCardById(cardId)).thenReturn(Optional.of(card));
        when(cardImageRepository.findAllByCard(card)).thenReturn(images);

        CardUserResponseDTO expectedResponse = new CardUserResponseDTO(
            new CardDetailResponseDTO(
                cardId,
                "macbook pro 교환 원해요",
                "thumbnail_img_url",
                CategoryEnum.ELECTRONICS,
                "apple macbook pro",
                true,
                createdDate,
                modifiedDate,
                0,
                PriceRange.PRICE_RANGE_FOUR,
                "상태 최상입니다.",
                CardStatus.TRADE_AVAILABLE,
                TradeType.DIRECT_DEALING,
                "서울",
                0,
                false,
                Arrays.asList(
                    new CardImageSingleReadResponseDTO("url1"),
                    new CardImageSingleReadResponseDTO("url2"),
                    new CardImageSingleReadResponseDTO("url3")
                )
            ),
            new UserSummaryResponseDTO(
                userId,
                "doby",
                "img_url"
            )
        );

        // when
        CardUserResponseDTO actualResponse = cardService.getCardById(token, cardId);

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("유저는 자신의 카드의 상태값을 변경할 수 있다.")
    void updateCardStatusById() {
        // given
        CardStatusUpdateRequestDTO statusUpdateRequest = new CardStatusUpdateRequestDTO(CardStatus.RESERVED);

        when(checkService.parseToken(token)).thenReturn(userId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findActiveCardById(cardId)).thenReturn(Optional.of(card));
        when(checkService.isEqual(userId, card.getUser().getUserId())).thenReturn(true);

        // when
        cardService.updateCardStatusById(token, cardId, statusUpdateRequest);

        // then
        assertThat(card.getStatus()).isEqualTo(statusUpdateRequest.status());
    }

    @Test
    @DisplayName("유저는 자신의 카드를 비활성화시킬 수 있다.")
    void deleteCardById() {
        // given
        when(checkService.parseToken(token)).thenReturn(userId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cardRepository.findActiveCardById(cardId)).thenReturn(Optional.of(card));
        when(checkService.isEqual(userId, card.getUser().getUserId())).thenReturn(true);

        // when
        cardService.deleteCardById(token, cardId);

        // then
        assertThat(card.getIsActive()).isEqualTo(false);
    }
}