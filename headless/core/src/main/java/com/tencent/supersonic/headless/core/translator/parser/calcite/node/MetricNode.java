package com.tencent.supersonic.headless.core.translator.parser.calcite.node;

import com.tencent.supersonic.common.pojo.enums.EngineType;
import com.tencent.supersonic.headless.core.translator.parser.calcite.S2CalciteSchema;
import com.tencent.supersonic.headless.core.translator.parser.s2sql.Metric;
import lombok.Data;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.validate.SqlValidatorScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class MetricNode extends SemanticNode {

    private Metric metric;
    private Map<String, SqlNode> aggNode = new HashMap<>();
    private Map<String, SqlNode> nonAggNode = new HashMap<>();
    private Map<String, SqlNode> measureFilter = new HashMap<>();
    private Map<String, String> aggFunction = new HashMap<>();

    public static SqlNode build(Metric metric, SqlValidatorScope scope, EngineType engineType)
            throws Exception {
        if (metric.getMetricTypeParams() == null || metric.getMetricTypeParams().getExpr() == null
                || metric.getMetricTypeParams().getExpr().isEmpty()) {
            return parse(metric.getName(), scope, engineType);
        }
        SqlNode sqlNode = parse(metric.getMetricTypeParams().getExpr(), scope, engineType);
        return buildAs(metric.getName(), sqlNode);
    }

    public static Boolean isMetricField(String name, S2CalciteSchema schema) {
        Optional<Metric> metric = schema.getMetrics().stream()
                .filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
        return metric.isPresent() && metric.get().getMetricTypeParams().isFieldMetric();
    }

    public static Boolean isMetricField(Metric metric) {
        return metric.getMetricTypeParams().isFieldMetric();
    }
}
