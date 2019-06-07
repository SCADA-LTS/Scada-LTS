<?php

		function addChildren($title) {
			global $depth;
			$depth++;
			
			for ($i=1; $i<=5; $i++) {
				$t = $title.'.'.$i;
				echo "$t\n";
								
				if ($depth<3) {
					addChildren($t);
				}
				
			}
			
			$depth--;			
		}


				addChildren("node");
