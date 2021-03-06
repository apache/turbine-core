# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Run the CodeWrestler
# https://github.com/hgschmie/CodeWrestler

# Add license to new files
# copy CodeWrestler-master.zip to ~/Dev/cw folder from https://github.com/hgschmie/CodeWrestler
python ~/Dev/cw/CodeWrestler.py -e conf/CodeWrestler.excludes --module=license.ReLicense --modopts='-f conf/checkstyle-license.txt -t java -n'
# Relicense existing files
python ~/Dev/cw/CodeWrestler.py -e conf/CodeWrestler.excludes --module=license.ReLicense --modopts='-f conf/checkstyle-license.txt -t java -e'

# Cleanup Java files
python ~/Dev/cw/CodeWrestler.py -e conf/CodeWrestler.excludes --module=java.TopFormatter
python ~/Dev/cw/CodeWrestler.py -e conf/CodeWrestler.excludes --module=xml.TopFormatter
python ~/Dev/cw/CodeWrestler.py -e conf/CodeWrestler.excludes --module=format.StripBlank
