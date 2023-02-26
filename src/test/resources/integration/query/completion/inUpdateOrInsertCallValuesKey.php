<?php

\DB::query()->from('users')->updateOrInsert(['first_name' => 'John'], ['<caret>' => 'value']);
