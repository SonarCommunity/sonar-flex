/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.flex.checks;

import com.google.common.collect.Maps;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.sonar.check.Rule;
import org.sonar.flex.FlexCheck;
import org.sonar.flex.FlexGrammar;
import org.sonar.flex.checks.utils.Variable;

@Rule(key = "S1481")
public class UnusedLocalVariableCheck extends FlexCheck {

  private static class LocalVariable {
    final AstNode declaration;
    int usages;

    private LocalVariable(AstNode declaration, int usages) {
      this.declaration = declaration;
      this.usages = usages;
    }
  }

  private static class Scope {
    private final Scope outerScope;
    private final Map<String, LocalVariable> variables;

    public Scope(Scope outerScope) {
      this.outerScope = outerScope;
      this.variables = Maps.newHashMap();
    }


    private void declare(AstNode astNode) {
      String identifier = astNode.getTokenValue();
      if (!variables.containsKey(identifier)) {
        variables.put(identifier, new LocalVariable(astNode, 0));
      }
    }

    private void use(AstNode astNode) {
      String identifier = astNode.getTokenValue();
      Scope scope = this;

      while (scope != null) {
        LocalVariable var = scope.variables.get(identifier);
        if (var != null) {
          var.usages++;
          return;
        }
        scope = scope.outerScope;
      }
    }
  }

  private Scope currentScope;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(
      FlexGrammar.FUNCTION_DEF,
      FlexGrammar.VARIABLE_DECLARATION_STATEMENT,
      FlexGrammar.QUALIFIED_IDENTIFIER);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    currentScope = null;
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (astNode.is(FlexGrammar.FUNCTION_DEF)) {
      currentScope = new Scope(currentScope);
    } else if (currentScope != null && astNode.is(FlexGrammar.VARIABLE_DECLARATION_STATEMENT)) {
      for (AstNode varIdentifier : Variable.getDeclaredIdentifiers(astNode)) {
        currentScope.declare(varIdentifier);
      }
    } else if (currentScope != null && astNode.is(FlexGrammar.QUALIFIED_IDENTIFIER)) {
      currentScope.use(astNode);
    }
  }

  @Override
  public void leaveNode(AstNode astNode) {
    if (astNode.is(FlexGrammar.FUNCTION_DEF)) {
      reportUnusedVariable();
      currentScope = currentScope.outerScope;
    }
  }


  private void reportUnusedVariable() {
    for (Map.Entry<String, LocalVariable> entry : currentScope.variables.entrySet()) {
      if (entry.getValue().usages == 0) {
        addIssue(MessageFormat.format("Remove this unused ''{0}'' local variable.", entry.getKey()), entry.getValue().declaration);
      }
    }
  }
}
