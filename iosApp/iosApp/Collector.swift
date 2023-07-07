//
//  Collector.swift
//  iosApp
//
//  Created by Swapnil on 7/6/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared

class Collector<T> : Kotlinx_coroutines_coreFlowCollector {
    let block:(T) -> Void

    init(block: @escaping (T) -> Void) {
        self.block = block
    }

    func emit(value: Any?) async throws {
        if let parsed = value as? T {
            DispatchQueue.main.async {
                self.block(parsed)
            }
        }
    }
}
