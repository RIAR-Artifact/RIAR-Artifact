package dynamicReduce.reducer;

import dynamicReduce.parser.FileParser;

import java.util.Collections;

public class RandomTraceReducer extends CallTraceReducer{

    public RandomTraceReducer(FileParser parser) {
        super(parser);
    }

    @Override
    protected void findFunctions() {
        super.findFunctions();
        Collections.shuffle(functions);
    }
}
