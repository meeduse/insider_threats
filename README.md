## Required
- Java 8 or greater
- Maven

## Compilation 
- run command : mvn clean test-compile

## Execution
- run command : mvn exec:java -Dexec.mainClass="apicalis.Tests_ProB" -Dexec.classpathScope=test

## Automatic run to reproduce experiments
- run script : test.sh (chmod +x test.sh ; ./test.sh)

## License

This project is licensed under the [Eclipse Public License v1.0 (EPL-1.0)](https://www.eclipse.org/legal/epl-v10.html), in line with the license of the ProB model checker.

© 2025 Akram Idani, Aurélien Pepin, and Mariem Triki  
Univ. Grenoble Alpes, Grenoble INP, CNRS, F-38000 Grenoble France  
Contact: akram.idani@univ-grenoble-alpes.fr, aurelien.pepin@grenoble-inp.org, mariem.triki@grenoble-inp.org

If you use this code in your academic work, please cite the following publication:

> A. Idani and A. Pepin and M. Triki., Insider Threat Simulation Through Ant Colonies and ProB. Proceedings of the 11th International Conference on Rigorous State-Based Methods (ABZ 2025). Springer, LNCS (to appear).

BibTeX:
```bibtex
@inproceedings{IdaniABZ25,
  author    = {A. Idani and A. Pepin and M. Triki},
  title     = {Insider Threat Simulation Through Ant Colonies and ProB},
  booktitle = {Proceedings of the 11th International Conference on Rigorous State-Based Methods (ABZ 2025)},
  series    = {LNCS},
  year      = {2025},
  publisher = {Springer},
  note      = {To appear}
}

