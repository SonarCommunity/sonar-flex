/*
 * SonarQube Flex Plugin
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.flex.duplications;

import net.sourceforge.pmd.cpd.Tokenizer;
import org.sonar.api.batch.AbstractCpdMapping;
import org.sonar.api.resources.Language;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.flex.core.Flex;

import java.nio.charset.Charset;

public class FlexCpdMapping extends AbstractCpdMapping {

  private final Flex language;
  private final Charset charset;

  public FlexCpdMapping(Flex flex, ModuleFileSystem fs) {
    this.language = flex;
    this.charset = fs.sourceCharset();
  }

  public Language getLanguage() {
    return language;
  }

  public Tokenizer getTokenizer() {
    return new FlexTokenizer(charset);
  }

}
