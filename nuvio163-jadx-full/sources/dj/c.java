package dj;

import javax.el.ExpressionFactory;
import javax.el.VariableMapper;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends VariableMapper {
    public c(e eVar) {
        ExpressionFactory expressionFactory = a.f13333a;
        Object obj = eVar.f13334a;
        expressionFactory.createValueExpression(obj, obj.getClass());
    }
}
