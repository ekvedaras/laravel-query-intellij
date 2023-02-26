<?php

\DB::query()->from('users')->insertGetId(['first_name' => 'value'], '<caret>');
