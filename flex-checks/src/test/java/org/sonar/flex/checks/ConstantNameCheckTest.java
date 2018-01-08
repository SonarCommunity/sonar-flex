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

import org.junit.Test;
import org.sonar.flex.FlexAstScanner;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

import java.io.File;

public class ConstantNameCheckTest {

  private ConstantNameCheck check = new ConstantNameCheck();

  @Test
  public void defaults(){
    SourceFile file = FlexAstScanner.scanSingleFile(new File("src/test/resources/checks/ConstantName.as"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(3).withMessage("Rename this constant 'p1' to match the regular expression " + check.format)
      .next().atLine(3).withMessage("Rename this constant 'p2' to match the regular expression " + check.format)
      .noMore();
  }

  @Test
  public void custom() {
    check.format = "[A-Z]+[0-9]+";

    SourceFile file = FlexAstScanner.scanSingleFile(new File("src/test/resources/checks/ConstantName.as"), check);
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().atLine(2)
      .next().atLine(3)
      .next().atLine(3)
      .noMore();
  }
}
