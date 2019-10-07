package org.scada_lts.dao;

import org.junit.Test;
import org.scada_lts.dao.batch.BatchProcessing;
import org.scada_lts.dao.batch.Limit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class BatchProcessingTest {

    @Test
    public void test_invoke_preprocess_then_all_objects_processed() {

        //given:
        final int size = 753;
        final String markProcessed = "processed";
        List<ObjectTest> data = initData(size);
        BatchTest batchTest = new BatchTest(data, markProcessed);

        //when:
        BatchProcessing.preprocess(batchTest::preprocess, batchTest::get, 133);
        boolean isProcessedAll = data.stream().allMatch(a -> markProcessed.equals(a.getValue()));

        //then:
        assertEquals(753, data.size());
        assertEquals(true, isProcessedAll);
    }

    private List<ObjectTest> initData(int size) {
        List<ObjectTest> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            data.add(new ObjectTest(i));
        }
        return data;
    }

    private class BatchTest {
        private final List<ObjectTest> data;
        private final String markProcessed;

        public BatchTest(List<ObjectTest> data, String markProcessed) {
            this.data = data;
            this.markProcessed = markProcessed;
        }

        public List<ObjectTest> get(long offset, Limit<Integer> limit) {
            if(offset > data.size())
                return Collections.emptyList();
            int end = (int)(offset + limit.get());
            if(end > data.size())
                end = data.size();
            return new ArrayList<>(data).subList((int)offset, end);
        }

        public void preprocess(List<ObjectTest> list) {
            list.forEach(a -> a.setValue(markProcessed));
        }
    }

    private class ObjectTest {
        private final long id;
        private String value;

        public ObjectTest(long id) {
            this.id = id;
            this.value = "noprocessed";
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
