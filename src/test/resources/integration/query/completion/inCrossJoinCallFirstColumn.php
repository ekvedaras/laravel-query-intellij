<?php

\DB::query()->from('users')->crossJoin('customers', '<caret>');
