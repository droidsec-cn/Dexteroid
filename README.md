# Dexteroid

This project here is only for showcase purposes and part of Dexteroid source code. It's not fully functional. We plan to release the complete source code in future after fixing all minor issues and critical bugs, if any.

For complete software architure and functionality, and scanning Android apps , please go to http://www.apkscanner.com

##1. Features

Dexteroid is a static analysis tool which uses reverse-engineered life cycle models to accurately capture the behaviors of Android components. It systematically derives event sequences from the models, and uses them to detect attacks launched by specific ordering of events. A prototype implementation of Dexteroid detects two types of attacks: (1) leakage of private information, and (2) sending SMS to premium-rate numbers. To validate effectiveness of the framework, a series of experiments are conducted on 1526 Google Play apps, 1259 Genome Malware apps, and a suite of benchmark apps called DroidBench. Our evaluation results show that the proposed framework is effective and efficient in terms of precision, recall, and execution time.

##2. Authors: Dexteroid

Dexteroid : Mohsin Junaid

Dexteroid uses Androguard for decompiling APK files which uses DAD as disassembler. 

Androguard : Anthony Desnos (desnos at t0t0.fr).
DAD : Geoffroy Gueguen (geoffroy dot gueguen at gmail dot com)

##3. Licenses

* Dexteroid

Copyright (C) 2016, Mohsin Junaid <mohsinjuni at gmail dot com>
All rights reserved.

Dexteroid is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
Dexteroid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.
 
You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

* Androguard

Copyright (C) 2012 - 2016, Anthony Desnos (desnos at t0t0.fr)
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS-IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

* DAD

Copyright (C) 2012 - 2016, Geoffroy Gueguen (geoffroy dot gueguen at gmail dot com)
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS-IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
