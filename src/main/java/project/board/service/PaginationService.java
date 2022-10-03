package project.board.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumber(int currentPageNumber, int totalPage) {
        int startPageNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);
        int endPageNumber = Math.min(startPageNumber + BAR_LENGTH, totalPage);

        return IntStream.range(startPageNumber, endPageNumber).boxed().toList();
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }

}
